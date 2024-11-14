package com.example.todoappshmr.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoappshmr.data.Task
import com.example.todoappshmr.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            repository.fetchTasksFromServer()
            _tasks.value = repository.tasks.value
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.addTask(task)
            loadTasks() // Перезагружаем список после добавления
        }
    }

    fun toggleTaskCompletion(taskId: Int) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(taskId)
            loadTasks() // Перезагружаем список после изменения
        }
    }

    fun removeTask(taskId: Int) {
        viewModelScope.launch {
            val task = _tasks.value.find { it.id == taskId }
            if (task != null) {
                repository.removeTask(task.id)
                loadTasks() // Перезагружаем список после удаления
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
            loadTasks() // Перезагружаем список после обновления
        }
    }
}