package com.example.todoappshmr.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoappshmr.model.Importance
import java.util.Date

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val importance: Importance,
    val deadline: Date? = null,
    val done: Boolean,
    val created_at: Date,
    val changed_at: Date? = null,
    val last_updated_by: String? = "test Phone"
)