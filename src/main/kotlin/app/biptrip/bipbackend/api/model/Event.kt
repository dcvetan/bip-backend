package app.biptrip.bipbackend.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class Event(
    val id: Int,
    val title: String,
    val description: String,

    @JsonProperty("start_time")
    val startTime: LocalDateTime,

    @JsonProperty("end_time")
    val endTime: LocalDateTime,
    val location: String,
    val price: Int,

    @JsonProperty("image_url")
    val imageUrl: String,
)
