package app.biptrip.bipbackend.api.model

data class LoginRequest(
    val email: String,
    val password: String,
)
