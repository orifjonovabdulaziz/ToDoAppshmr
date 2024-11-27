package com.example.todoappshmr.utils

import androidx.room.TypeConverter
import com.example.todoappshmr.data.model.NetworkTaskModel
import com.example.todoappshmr.data.model.Task
import com.example.todoappshmr.data.model.WrapperTaskrequest
import com.example.todoappshmr.ui.model.Importance
import java.util.Date

object Converters {
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


    fun mapTaskToTaskRequest(task: Task): WrapperTaskrequest {
        return WrapperTaskrequest(
            element = NetworkTaskModel(
                id = task.id.toString(),
                text = task.text,
                importance = task.importance.name.lowercase(),
                done = task.done,
                created_at = task.created_at.time,
                changed_at = task.changed_at?.time ?: System.currentTimeMillis(),
                last_updated_by = task.last_updated_by ?: "unknown",
                deadline = task.deadline?.time
            )
        )
    }

    fun mapTaskRequestToTask(networkTaskModel: NetworkTaskModel): Task {
        return Task(
            id = networkTaskModel.id.toIntOrNull() ?: 0,
            text = networkTaskModel.text,
            importance = Importance.valueOf(networkTaskModel.importance.uppercase()),
            done = networkTaskModel.done,
            created_at = Date(networkTaskModel.created_at),
            changed_at = Date(networkTaskModel.changed_at),
            last_updated_by = networkTaskModel.last_updated_by,
            deadline = networkTaskModel.deadline?.let { Date(it) }
        )
    }

}