package com.example.todoappshmr

import AppTheme
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.example.todoappshmr.data.DatabaseProvider
import com.example.todoappshmr.navigation.AppNavigation
import com.example.todoappshmr.repository.TaskViewModel
import com.example.todoappshmr.data.TaskViewModelFactory
import com.example.todoappshmr.network.RetrofitClient
import com.example.todoappshmr.repository.TaskRepository

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = DatabaseProvider.getDatabase(this)
        val apiService = RetrofitClient.create()
        val repository = TaskRepository(database, apiService, this)
        val viewModel: TaskViewModel by viewModels {
            TaskViewModelFactory(repository)
        }

        setContent {
            AppTheme {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}

