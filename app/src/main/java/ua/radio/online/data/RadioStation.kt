package ua.radio.online.data

import kotlinx.serialization.Serializable

@Serializable
data class RadioStation(
    val id: String = "",
    val name: String,
    val streamUrl: String,
    val logoUrl: String? = null,
    val description: String? = null,
    val isActive: Boolean = true,
    val sortOrder: Int = 0
)
