package app.biptrip.bipbackend.api.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Ticket(
        val id: Int,
        @JsonProperty("event_id")
        val eventId: Int,
        @JsonProperty("user_id")
        val userId: Int,
        @JsonProperty("qr_url")
        val qrUrl: String
)
