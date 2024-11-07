package com.example.todoappshmr.utils

import com.example.todoappshmr.model.Importance

fun getImportanceFromString(priority: String): Importance {
    return when (priority) {
        "Низкий" -> Importance.LOW
        "Нет" -> Importance.NORMAL
        "Высокий" -> Importance.HIGH
        else -> Importance.NORMAL
    }
}