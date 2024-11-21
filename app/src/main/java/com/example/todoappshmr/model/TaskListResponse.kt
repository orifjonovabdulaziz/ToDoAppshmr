package com.example.todoappshmr.model

data class TaskListResponse(
    val list: List<NetworkTaskModel>,
    val revision: Int
)



