package com.example.todoappshmr.network

import com.example.todoappshmr.model.TaskListResponse
import com.example.todoappshmr.model.NetworkTaskModel
import com.example.todoappshmr.model.TaskResponse
import retrofit2.Response
import retrofit2.http.*

data class WrapperTasrequest(
        val element: NetworkTaskModel
        )
interface ApiService {
    @GET("todo/list/")
    suspend fun getTasks(): Response<TaskListResponse>

    @POST("todo/list/")
    suspend fun addTask(@Body task: WrapperTasrequest): Response<TaskResponse>

    @PUT("todo/list/{id}")
    suspend fun updateTask(@Path("id") id: String, @Body task: WrapperTasrequest): Response<TaskResponse>

    @DELETE("todo/list/{id}")
    suspend fun deleteTask(@Path("id") id: String): Response<Void>
}