package com.example.todoappshmr.repository


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.todoappshmr.data.AppDatabase
import com.example.todoappshmr.data.Task
import com.example.todoappshmr.model.Importance
import com.example.todoappshmr.model.TaskRequest
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

    // Хранилище для отслеживания списка задач
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> get() = _tasks

    private val _networkStatusFlow = MutableSharedFlow<String>()
    val networkStatusFlow: SharedFlow<String> = _networkStatusFlow.asSharedFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (isNetworkAvailable()) {
                fetchTasksFromServer()
            } else {
                _networkStatusFlow.emit("No internet connection. Working in offline mode.")
                _tasks.value = database.taskDao().getAllTasks()
            }
        }
    }

    suspend fun fetchTasksFromServer() {
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTasks()
                if (response.isSuccessful && response.body() != null) {
                    val tasksFromServer = response.body()!!.list.map { mapTaskRequestToTask(it) }

                    // Добавляем каждый элемент в базу данных поочередно
                    tasksFromServer.forEach { task ->
                        database.taskDao().insertTask(task)
                    }

                    _tasks.value =
                        database.taskDao().getAllTasks() // Обновляем список задач из базы данных
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
            // Обновление задачи в локальной базе данных
            database.taskDao().updateTask(task)

            // Попытка отправить обновление на сервер
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

            // Обновляем список задач из базы данных
            _tasks.value = database.taskDao().getAllTasks()
        }
    }


    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun mapTaskToTaskRequest(task: Task): WrapperTasrequest {
        return WrapperTasrequest(
            element = TaskRequest(
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

    private fun mapTaskRequestToTask(taskRequest: TaskRequest): Task {
        return Task(
            id = taskRequest.id.toIntOrNull() ?: 0, // Обрабатываем возможное преобразование строки в Int

            text = taskRequest.text,
            importance = Importance.valueOf(taskRequest.importance.uppercase()), // Преобразуем строку в enum
            done = taskRequest.done,
            created_at = Date(taskRequest.created_at),
            changed_at = Date(taskRequest.changed_at),
            last_updated_by = taskRequest.last_updated_by,
            deadline = taskRequest.deadline?.let { Date(it) }
        )
    }

}
