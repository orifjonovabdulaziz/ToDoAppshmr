package com.example.todoappshmr.domain.model

import androidx.room.PrimaryKey
import com.example.todoappshmr.ui.model.Importance
import com.example.todoappshmr.utils.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class TaskDomain(
    @PrimaryKey(autoGenerate = false)
    @SerialName("id") val id: Int,
    @SerialName("text") val text: String,
    @SerialName("importance") val importance: Importance,
    @Serializable(with = DateSerializer::class)
    @SerialName("deadline") val deadline: Date? = null,
    @SerialName("done") val done: Boolean,
    @Serializable(with = DateSerializer::class)
    @SerialName("created_at") val created_at: Date,
    @Serializable(with = DateSerializer::class)
    @SerialName("changed_at") val changed_at: Date? = null,
    @SerialName("last_updated_by") val last_updated_by: String? = "test Phone"
)