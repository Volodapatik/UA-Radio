package ua.radio.online.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RadioStation(
    val id: String = "",
    val name: String,
    @SerialName("stream_url") val streamUrl: String,
    @SerialName("logo_url") val logoUrl: String? = null,
    val description: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("sort_order") val sortOrder: Int = 0
)
