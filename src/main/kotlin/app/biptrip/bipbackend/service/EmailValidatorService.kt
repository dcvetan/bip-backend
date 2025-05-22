package app.biptrip.bipbackend.service

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class EmailValidatorService(private val webClient: WebClient) {

    fun validateAcademicEmail(email: String): Boolean {
        val response = webClient.post()
            .uri("https://api.apyhub.com/validate/email/academic")
            .header("Content-Type", "application/json")
            .header("apy-token", "APY0v5h71tU0QbX6POOpCz3xX6DNogSSe13IGhAu9YYoHPmu67YQfZVeDbW9zwiD")
            .bodyValue(mapOf("email" to email))
            .retrieve()
            .bodyToMono<Response>()
            .block()

        return response!!.data
    }

    data class Response(
            val data: Boolean
    )

}