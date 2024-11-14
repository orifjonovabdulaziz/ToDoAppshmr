package com.example.todoappshmr.model

import com.example.todoappshmr.data.Task
import java.util.Date

data class TaskRequest(
    val id: String,
    val text: String,
    val importance: String,
    val done: Boolean,
    val created_at: Long,
    val changed_at: Long,
    val last_updated_by: String,
    val deadline: Long?,

)



