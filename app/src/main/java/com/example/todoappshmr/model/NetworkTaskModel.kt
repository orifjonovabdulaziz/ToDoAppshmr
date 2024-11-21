package com.example.todoappshmr.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class NetworkTaskModel(
    @SerialName("id") val id: String,
    @SerialName("text") val text: String,
    @SerialName("importance") val importance: String,
    @SerialName("done") val done: Boolean,
    @SerialName("created_at") val created_at: Long,
    @SerialName("changed_at") val changed_at: Long,
    @SerialName("last_updated_by") val last_updated_by: String,
    @SerialName("deadline") val deadline: Long?,

)






