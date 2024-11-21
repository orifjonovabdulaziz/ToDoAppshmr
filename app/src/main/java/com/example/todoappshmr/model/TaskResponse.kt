package com.example.todoappshmr.model

data class TaskResponse(
    val status: String,
    val task: NetworkTaskModel?,
    val revision: Int
)