package com.example.todoappshmr.repository


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.todoappshmr.data.AppDatabase
import com.example.todoappshmr.data.Task
import com.example.todoappshmr.model.Importance
import com.example.todoappshmr.model.NetworkTaskModel
import com.example.todoappshmr.network.ApiService
import com.example.todoappshmr.network.WrapperTasrequest
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.lang.Exception
import java.util.Date

class TaskRepository(
    private val database: AppDatabase,
    private val apiService: ApiService,
    private val context: Context
) {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> get() = _tasks

    private val _networkStatusFlow = MutableSharedFlow<String>()
    val networkStatusFlow: SharedFlow<String> = _networkStatusFlow.asSharedFlow()

    private var networkJob: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        CoroutineScope(Dispatchers.Main).launch {
            _networkStatusFlow.emit("Error occurred: ${exception.message}")
        }
        exception.printStackTrace()
    }

    fun startListening() {
        networkJob = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            while (isActive) {
                if (isNetworkAvailable()) {
                    fetchTasksFromServer()
                } else {
                    _networkStatusFlow.emit("No internet connection. Working in offline mode.")
                    _tasks.value = database.taskDao().getAllTasks()
                }
                delay(10_000)
            }
        }
    }

    fun stopListening() {
        networkJob?.cancel()
    }

    suspend fun fetchTasksFromServer() {
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTasks()
                if (response.isSuccessful && response.body() != null) {
                    val tasksFromServer = response.body()!!.list.map { mapTaskRequestToTask(it) }

                    tasksFromServer.forEach { task ->
                        database.taskDao().insertTask(task)
                    }
                    val revision = response.body()!!.revision
                    PreferencesManager.saveRevision(context, revision)

                    _tasks.value =
                        database.taskDao().getAllTasks()
                    _networkStatusFlow.emit("Tasks updated from server.")
                } else {
                    _networkStatusFlow.emit("Failed to fetch from server, using local data.")
                    _tasks.value = database.taskDao().getAllTasks()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _networkStatusFlow.emit("Error fetching from server, using local data.")
                _tasks.value = database.taskDao().getAllTasks()
            }
        }
    }


    suspend fun addTask(task: Task) {
        withContext(Dispatchers.IO) {
            database.taskDao().insertTask(task)
            if (isNetworkAvailable()) {
                try {
                    val response = apiService.addTask(mapTaskToTaskRequest(task))
                    if (!response.isSuccessful) throw Exception("Error adding task to server.")
                    val revision = response.body()!!.revision
                    PreferencesManager.saveRevision(context, revision)
                } catch (e: Exception) {
                    e.printStackTrace()
                    _networkStatusFlow.emit("Task added locally. Sync will occur when network is available.")
                }
            } else {
                _networkStatusFlow.emit("No internet connection. Task added locally.")
            }
            _tasks.value = database.taskDao().getAllTasks()
        }
    }

    suspend fun toggleTaskCompletion(taskId: Int) {
        withContext(Dispatchers.IO) {
            val task = database.taskDao().getTaskById(taskId)
            if (task != null) {
                val updatedIsCompleted = !task.done
                database.taskDao().updateTaskCompletion(taskId, updatedIsCompleted)
                if (isNetworkAvailable()) {
                    try {
                        val updatedTask = (task.copy(done = updatedIsCompleted))
                        val response = apiService.updateTask(taskId.toString(), mapTaskToTaskRequest(updatedTask))
                        if (!response.isSuccessful) throw Exception("Error updating task on server.")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _networkStatusFlow.emit("Task updated locally. Sync will occur when network is available.")
                    }
                } else {
                    _networkStatusFlow.emit("No internet connection. Task updated locally.")
                }
                _tasks.value = database.taskDao().getAllTasks()
            }
        }
    }

    suspend fun removeTask(taskId: Int) {
        withContext(Dispatchers.IO) {
            val task = database.taskDao().getTaskById(taskId)
            if (task != null) {
                database.taskDao().deleteTask(task)
                if (isNetworkAvailable()) {
                    try {
                        val response = apiService.deleteTask(taskId.toString())
                        if (!response.isSuccessful) throw Exception("Error deleting task on server.")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _networkStatusFlow.emit("Task deleted locally. Sync will occur when network is available.")
                    }
                } else {
                    _networkStatusFlow.emit("No internet connection. Task deleted locally.")
                }
                _tasks.value = database.taskDao().getAllTasks()
            }
        }
    }

    suspend fun updateTask(task: Task) {
        withContext(Dispatchers.IO) {
            database.taskDao().updateTask(task)

            if (isNetworkAvailable()) {
                try {
                    val response = apiService.updateTask(task.id.toString(), mapTaskToTaskRequest(task))
                    if (!response.isSuccessful) {
                        throw Exception("Ошибка при обновлении задачи на сервере")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _networkStatusFlow.emit("Задача обновлена локально. Синхронизация произойдет при появлении сети.")
                }
            } else {
                _networkStatusFlow.emit("Нет подключения к интернету. Задача обновлена локально.")
            }

            _tasks.value = database.taskDao().getAllTasks()
        }
    }

    suspend fun getLastTaskId(): Int? {
        return database.taskDao().getLastTaskId()
    }


    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun mapTaskToTaskRequest(task: Task): WrapperTasrequest {
        return WrapperTasrequest(
            element = NetworkTaskModel(
            id = task.id.toString(),
            text = task.text,
            importance = task.importance.name.lowercase(),
            done = task.done,
            created_at = task.created_at.time,
            changed_at = task.changed_at?.time ?: System.currentTimeMillis(),
            last_updated_by = task.last_updated_by ?: "unknown",
            deadline = task.deadline?.time
        ))
    }

    private fun mapTaskRequestToTask(networkTaskModel: NetworkTaskModel): Task {
        return Task(
            id = networkTaskModel.id.toIntOrNull() ?: 0,
            text = networkTaskModel.text,
            importance = Importance.valueOf(networkTaskModel.importance.uppercase()),
            done = networkTaskModel.done,
            created_at = Date(networkTaskModel.created_at),
            changed_at = Date(networkTaskModel.changed_at),
            last_updated_by = networkTaskModel.last_updated_by,
            deadline = networkTaskModel.deadline?.let { Date(it) }
        )
    }

}
