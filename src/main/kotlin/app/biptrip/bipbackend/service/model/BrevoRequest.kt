package app.biptrip.bipbackend.service.model

data class BrevoRequest(
        val sender: BrevoSender,
        val to: List<BrevoRecipient>,
        val subject: String,
        val htmlContent: String,
)

data class BrevoSender(
        val name: String,
        val email: String
)

data class BrevoRecipient(
        val email: String
)