package com.example.todoappshmr.domain.usecases

import android.content.Context
import android.util.Log
import com.example.todoappshmr.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class NetworkUseCase(private val context: Context) {

    private val _networkStatus = MutableStateFlow(false)
    val networkStatus: StateFlow<Boolean> = _networkStatus.asStateFlow()

    private var monitoringJob: Job? = null

    fun startMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                val isConnected = NetworkUtils.isNetworkAvailable(context)
                Log.d("isConnected status_network", "$isConnected")
                _networkStatus.emit(isConnected)
                delay(10_000) // Проверка каждые 10 секунд
            }
        }
    }

    fun stopMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = null
    }
}
