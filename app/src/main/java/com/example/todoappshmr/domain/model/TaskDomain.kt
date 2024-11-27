package com.example.todoappshmr.domain.model

import androidx.room.PrimaryKey
import com.example.todoappshmr.ui.model.Importance

import java.util.Date

data class TaskDomain(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val text: String,
    val importance: Importance,
    val deadline: Date? = null,
    val done: Boolean,
    val created_at: Date,
    val changed_at: Date? = null,
    val last_updated_by: String? = "test Phone"
)