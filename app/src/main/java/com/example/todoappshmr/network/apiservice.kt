package com.example.todoappshmr.network

import com.example.todoappshmr.model.TaskListResponse
import com.example.todoappshmr.model.TaskRequest
import com.example.todoappshmr.model.TaskResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("/list")
    suspend fun getTasks(): Response<TaskListResponse>

    @POST("/list")
    suspend fun addTask(@Body task: TaskRequest): Response<TaskResponse>

    @PUT("/list/{id}")
    suspend fun updateTask(@Path("id") id: String, @Body task: TaskRequest): Response<TaskResponse>

    @DELETE("/list/{id}")
    suspend fun deleteTask(@Path("id") id: String): Response<Void>
}