package com.example.todoappshmr.ioc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoappshmr.domain.usecases.NetworkUseCase
import com.example.todoappshmr.domain.usecases.TaskUseCase
import com.example.todoappshmr.ui.stateholders.TaskViewModel

class TaskViewModelFactory(private val repository: TaskUseCase, private val networkRepository: NetworkUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository, networkRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}