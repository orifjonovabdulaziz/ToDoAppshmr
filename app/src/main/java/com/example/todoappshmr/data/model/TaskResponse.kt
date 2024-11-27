package com.example.todoappshmr.data.model

data class TaskResponse(
    val status: String,
    val task: NetworkTaskModel?,
    val revision: Int
)