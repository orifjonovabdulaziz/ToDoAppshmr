package com.example.todoappshmr.domain.usecases


import android.content.Context
import com.example.todoappshmr.utils.NetworkUtils
import com.example.todoappshmr.data.datasource.Room.TaskLocalDataSource
import com.example.todoappshmr.data.datasource.network.TaskRemoteDataSource
import com.example.todoappshmr.data.datasource.sharedPreferences.PreferencesManager
import com.example.todoappshmr.data.model.Task

import com.example.todoappshmr.utils.Converters
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.lang.Exception

class TaskUseCase(
    private val localDataSource: TaskLocalDataSource,
    private val remoteDataSource: TaskRemoteDataSource,
    private val networkRepository: NetworkUseCase,
    private val context: Context
) {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> get() = _tasks

    private val _networkStatusFlow = MutableSharedFlow<String>()



    init {
        observeNetworkChanges()
    }

    private fun observeNetworkChanges() {
        CoroutineScope(Dispatchers.IO).launch {
            networkRepository.networkStatus.collect { isConnected ->
//                Log.d("isConnected status", "$isConnected")
//                if (isConnected) {
                    fetchTasksFromServer()
//                } else {
//                    _tasks.value = localDataSource.getAllTasks()
//                }
            }
        }
    }

    suspend fun fetchTasksFromServer() {
        withContext(Dispatchers.IO) {
            try {
                _tasks.value = localDataSource.getAllTasks()
                val response = remoteDataSource.getTasks()
                if (response.isSuccessful && response.body() != null) {
                    val tasksFromServer = response.body()!!.list.map { Converters.mapTaskRequestToTask(it) }

                    tasksFromServer.forEach { task ->
                        localDataSource.insertTask(task)
                    }
                    val revision = response.body()!!.revision
                    PreferencesManager.saveRevision(context, revision)

                    _tasks.value =
                        localDataSource.getAllTasks()
                    _networkStatusFlow.emit("Tasks updated from server.")
                } else {
                    _networkStatusFlow.emit("Failed to fetch from server, using local data.")
                    _tasks.value = localDataSource.getAllTasks()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _networkStatusFlow.emit("Error fetching from server, using local data.")
                _tasks.value = localDataSource.getAllTasks()
            }
        }
    }


    suspend fun addTask(task: Task) {
        withContext(Dispatchers.IO) {
            localDataSource.insertTask(task)
            if (NetworkUtils.isNetworkAvailable(context)) {
                try {
                    val response = remoteDataSource.addTask(Converters.mapTaskToTaskRequest(task))
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
            _tasks.value = localDataSource.getAllTasks()
        }
    }

    suspend fun toggleTaskCompletion(taskId: Int) {
        withContext(Dispatchers.IO) {
            val task = localDataSource.getTaskById(taskId)
            if (task != null) {
                val updatedIsCompleted = !task.done
                localDataSource.updateTaskCompletion(taskId, updatedIsCompleted)
                if (NetworkUtils.isNetworkAvailable(context)) {
                    try {
                        val updatedTask = (task.copy(done = updatedIsCompleted))
                        val response = remoteDataSource.updateTask(taskId.toString(), Converters.mapTaskToTaskRequest(updatedTask))
                        if (!response.isSuccessful) throw Exception("Error updating task on server.")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _networkStatusFlow.emit("Task updated locally. Sync will occur when network is available.")
                    }
                } else {
                    _networkStatusFlow.emit("No internet connection. Task updated locally.")
                }
                _tasks.value = localDataSource.getAllTasks()
            }
        }
    }

    suspend fun removeTask(taskId: Int) {
        withContext(Dispatchers.IO) {
            val task = localDataSource.getTaskById(taskId)
            if (task != null) {
                localDataSource.deleteTask(task)
                if (NetworkUtils.isNetworkAvailable(context)) {
                    try {
                        val response = remoteDataSource.deleteTask(taskId.toString())
                        if (!response.isSuccessful) throw Exception("Error deleting task on server.")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _networkStatusFlow.emit("Task deleted locally. Sync will occur when network is available.")
                    }
                } else {
                    _networkStatusFlow.emit("No internet connection. Task deleted locally.")
                }
                _tasks.value = localDataSource.getAllTasks()
            }
        }
    }

    suspend fun updateTask(task: Task) {
        withContext(Dispatchers.IO) {
            localDataSource.updateTask(task)

            if (NetworkUtils.isNetworkAvailable(context)) {
                try {
                    val response = remoteDataSource.updateTask(task.id.toString(), Converters.mapTaskToTaskRequest(task))
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

            _tasks.value = localDataSource.getAllTasks()
        }
    }

    suspend fun getLastTaskId(): Int? {
        return localDataSource.getLastTaskId()
    }








}
