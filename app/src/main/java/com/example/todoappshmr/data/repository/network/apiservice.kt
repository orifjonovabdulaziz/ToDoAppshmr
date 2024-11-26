package com.example.todoappshmr.data.repository.network

import com.example.todoappshmr.data.model.TaskListResponse
import com.example.todoappshmr.data.model.TaskResponse
import com.example.todoappshmr.data.model.WrapperTaskrequest
import retrofit2.Response
import retrofit2.http.*


interface ApiService {
    @GET("todo/list/")
    suspend fun getTasks(): Response<TaskListResponse>

    @POST("todo/list/")
    suspend fun addTask(@Body task: WrapperTaskrequest): Response<TaskResponse>

    @PUT("todo/list/{id}")
    suspend fun updateTask(@Path("id") id: String, @Body task: WrapperTaskrequest): Response<TaskResponse>

    @DELETE("todo/list/{id}")
    suspend fun deleteTask(@Path("id") id: String): Response<Void>
}