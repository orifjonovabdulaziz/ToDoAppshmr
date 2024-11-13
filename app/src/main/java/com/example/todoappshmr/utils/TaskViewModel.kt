package com.example.todoappshmr.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoappshmr.model.ToDoItem
import com.example.todoappshmr.repository.TodoItemsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    private val _tasks = MutableStateFlow<List<ToDoItem>>(emptyList())
    val tasks: StateFlow<List<ToDoItem>> = _tasks.asStateFlow()
    val repository = TodoItemsRepository()

    init {
        viewModelScope.launch {
            _tasks.value = repository.getTodoItems()
        }
    }


    fun addTask(task: ToDoItem) {
        viewModelScope.launch {
            repository.addTodoItem(task)
            _tasks.value = repository.getTodoItems()
        }
    }
    fun removeTask(taskId: Int) {
        viewModelScope.launch {
            _tasks.value = _tasks.value.filter { it.id != taskId }
        }
    }

    fun toggleTaskCompletion(taskId: Int) {
        viewModelScope.launch {
            val updatedTasks = _tasks.value.map { task ->
                if (task.id == taskId) {
                    task.copy(isCompleted = !task.isCompleted)
                } else {
                    task
                }
            }
            _tasks.value = updatedTasks
        }
    }

    fun updateTask(updatedTask: ToDoItem) {
        _tasks.value = _tasks.value.map { task ->
            if (task.id == updatedTask.id) updatedTask else task
        }
    }
}
