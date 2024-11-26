package com.example.todoappshmr.ui.stateholders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoappshmr.data.model.Task
import com.example.todoappshmr.domain.usecases.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    init {
        viewModelScope.launch {
            repository.tasks.collect { taskList ->
                _tasks.value = taskList
            }
        }
    }



    suspend fun getLastTaskId(): Int {
        return runBlocking {
            repository.getLastTaskId() ?: 0
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            try {
                repository.addTask(task)
            } catch (e: Exception) {
                _errorState.value = e.message
            }
        }
    }

    fun toggleTaskCompletion(taskId: Int) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(taskId)
        }
    }

    fun removeTask(taskId: Int) {
        viewModelScope.launch {
            repository.removeTask(taskId)
            _tasks.value = _tasks.value.filter { it.id != taskId }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun getTaskById(taskId: Int): Task? {
        return tasks.value.find { it.id == taskId }
    }

}