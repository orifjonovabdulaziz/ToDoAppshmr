package com.example.todoappshmr.data.datasource.network

import com.example.todoappshmr.data.model.WrapperTaskrequest
import com.example.todoappshmr.data.repository.network.ApiService


class TaskRemoteDataSource(private val apiService: ApiService){
    suspend fun getTasks() = apiService.getTasks()
    suspend fun addTask(task: WrapperTaskrequest) = apiService.addTask(task)
    suspend fun updateTask(id: String, task: WrapperTaskrequest) = apiService.updateTask(id, task)
    suspend fun deleteTask(id: String) = apiService.deleteTask(id)

}