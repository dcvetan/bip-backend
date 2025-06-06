package app.biptrip.bipbackend.service

import app.biptrip.bipbackend.service.model.BrevoRecipient
import app.biptrip.bipbackend.service.model.BrevoRequest
import app.biptrip.bipbackend.service.model.BrevoSender
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.time.Duration
import java.time.LocalDateTime

@Service
class EmailService(
        private val webClient: WebClient,

        @Value("\${secrets.api-key.brevo}")
        private val apiKey: String,
) {

    suspend fun sendVerificationEmail(toEmail: String, verificationLink: String, date: LocalDateTime, location: String) {
        try {
            val template = loadTemplate("qr-code-email.html")
            val formattedDate = date.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy."))
            val formattedTime = date.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"))
            val htmlContent = template
                .replace("%d", formattedDate)
                .replace("%t", formattedTime)
                .replace("%l", location)
                .replace("%s", verificationLink)

            webClient.post()
                .uri("https://api.brevo.com/v3/smtp/email")
                .header("api-key", apiKey)
                .bodyValue(BrevoRequest(
                        sender = BrevoSender(
                                name = "MeetERA",
                                email = "noreply@quicklingo.app"
                        ),
                        to = listOf(BrevoRecipient(toEmail)),
                        subject = "QR Code Receipt",
                        htmlContent = htmlContent
                ))
                .exchangeToMono { it.bodyToMono<String>() }
                .log()
                .timeout(Duration.ofSeconds(10))
                .retry(1)
                .awaitSingle()
        } catch (e: Exception) {
            throw RuntimeException("An error occurred: ${e.message}", e)
        }
    }

    private fun loadTemplate(templatePath: String): String {
        return EmailService::class.java.getResource("/templates/$templatePath")
                   ?.readText()
               ?: throw IllegalStateException("Email template $templatePath not found")
    }

}