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
import com.example.todoappshmr.ioc.TaskViewModelFactory
import com.example.todoappshmr.data.datasource.Room.TaskLocalDataSource
import com.example.todoappshmr.data.datasource.network.RetrofitClient
import com.example.todoappshmr.data.datasource.network.TaskRemoteDataSource
import com.example.todoappshmr.domain.usecases.NetworkUseCase
import com.example.todoappshmr.domain.usecases.TaskUseCase

class MainActivity : FragmentActivity() {

    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(
            repository = TaskUseCase(
                localDataSource = TaskLocalDataSource(DatabaseProvider.getDatabase(this)),
                remoteDataSource = TaskRemoteDataSource(RetrofitClient.create(this)),
                networkRepository = NetworkUseCase(this),
                this
            ),
            networkRepository = NetworkUseCase(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                AppNavigation(viewModel = viewModel)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.startListening() // Начинаем мониторить состояние сети
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopListening() // Останавливаем мониторинг состояния сети
    }
}



