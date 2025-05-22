package app.biptrip.bipbackend.api

import app.biptrip.bipbackend.api.model.Ticket
import app.biptrip.bipbackend.repository.EventRepository
import app.biptrip.bipbackend.repository.TicketRepository
import app.biptrip.bipbackend.repository.UserRepository
import app.biptrip.bipbackend.service.EmailService
import app.biptrip.bipbackend.service.QrService
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/ticket")
class TicketController(
        private val ticketRepository: TicketRepository,
        private val qrService: QrService,
        private val emailService: EmailService,
        private val userRepository: UserRepository,
        private val eventRepository: EventRepository
) {

    @GetMapping
    fun get(@RequestParam("userId") userId: Int): ResponseEntity<List<Ticket>> {
        val tickets = ticketRepository.findByUserId(userId)
        return ResponseEntity.ok(tickets.map { ticket ->
            Ticket(
                    id = ticket.id,
                    eventId = ticket.eventId,
                    userId = ticket.userId,
                    qrUrl = ticket.qrUrl
            )
        })
    }

    @PostMapping
    suspend fun buyTicket(@RequestBody request: BuyTicketRequest): ResponseEntity<Ticket> {

        val ticket = ticketRepository.insert(request.userId, request.eventId)
        val qrUrl = qrService.generateQrCode("https://bip-backend-b1ew.onrender.com/ticket/validate?ticketId=${ticket!!.id}")
        ticketRepository.addQrUrl(ticket.id, qrUrl)

        val user = userRepository.findUserById(request.userId)

        emailService.sendVerificationEmail(user!!.email, qrUrl)

        eventRepository.decrementTickets(request.eventId)

        return ResponseEntity.ok(
                Ticket(
                        id = ticket.id,
                        eventId = ticket.eventId,
                        userId = ticket.userId,
                        qrUrl = qrUrl
                )
        )
    }

    @GetMapping("/validate")
    fun validateTicket(@RequestParam ticketId: Int): ResponseEntity<Unit> {
        ticketRepository.findByTicketId(ticketId)
        ?: return ResponseEntity.notFound().build()

        ticketRepository.deleteById(ticketId)

        return ResponseEntity.ok().build()
    }

    data class BuyTicketRequest(
            @JsonProperty("user_id")
            val userId: Int,
            @JsonProperty("event_id")
            val eventId: Int
    )

}