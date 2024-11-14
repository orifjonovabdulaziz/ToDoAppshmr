package com.example.todoappshmr.utils

import androidx.room.TypeConverter
import com.example.todoappshmr.model.Importance
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromImportance(value: String): Importance {
        return Importance.values().first { it.value == value }
    }

    @TypeConverter
    fun importanceToString(importance: Importance): String {
        return importance.value
    }
}