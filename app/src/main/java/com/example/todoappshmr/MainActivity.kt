package com.example.todoappshmr

import AppTheme
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.example.todoappshmr.data.datasource.Room.DatabaseProvider
import com.example.todoappshmr.ui.navigation.AppNavigation
import com.example.todoappshmr.ui.stateholders.TaskViewModel
import com.example.todoappshmr.data.TaskViewModelFactory
import com.example.todoappshmr.data.datasource.Room.TaskLocalDataSource
import com.example.todoappshmr.data.datasource.network.RetrofitClient
import com.example.todoappshmr.data.datasource.network.TaskRemoteDataSource
import com.example.todoappshmr.domain.usecases.TaskRepository

class MainActivity : FragmentActivity() {
    private lateinit var repository: TaskRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = DatabaseProvider.getDatabase(this)
        val apiService = RetrofitClient.create(this)

        val localDataSource = TaskLocalDataSource(database)
        val remoteDataSource = TaskRemoteDataSource(apiService)
        repository = TaskRepository(localDataSource, remoteDataSource, this)
        val viewModel: TaskViewModel by viewModels {
            TaskViewModelFactory(repository)
        }


        setContent {
            AppTheme {
                AppNavigation(viewModel = viewModel)
            }
        }


    }

    override fun onStart() {
        super.onStart()
        repository.startListening()
    }

    override fun onStop() {
        super.onStop()
        repository.stopListening()
    }
}

