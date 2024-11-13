package com.example.todoappshmr.model

import java.util.Date

enum class Importance(val value: String) {
    NORMAL("Нет"),
    LOW("Низкий"),
    HIGH("Высокий")
}

data class ToDoItem(
    val id: Int,
    val text: String,
    val importance: Importance,
    val deadline: Date? = null,
    val isCompleted: Boolean,
    val createdAt: Date,
    val modifiedAt: Date? = null
)

