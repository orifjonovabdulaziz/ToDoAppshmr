package com.example.todoappshmr.utils

import com.example.todoappshmr.ui.model.Importance

fun getImportanceFromString(priority: String): Importance {
    return when (priority) {
        "Низкий" -> Importance.LOW
        "Нет" -> Importance.BASIC
        "Высокий" -> Importance.IMPORTANT
        else -> Importance.BASIC
    }
}