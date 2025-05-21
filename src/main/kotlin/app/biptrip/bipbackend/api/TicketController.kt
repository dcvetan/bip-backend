package app.biptrip.bipbackend.api

import app.biptrip.bipbackend.api.model.Ticket
import app.biptrip.bipbackend.jooq.tables.records.TicketsRecord
import app.biptrip.bipbackend.repository.TicketRepository
import app.biptrip.bipbackend.repository.UserRepository
import app.biptrip.bipbackend.service.EmailService
import app.biptrip.bipbackend.service.QrService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/ticket")
class TicketController(
        private val ticketRepository: TicketRepository,
        private val qrService: QrService,
        private val emailService: EmailService,
        private val userRepository: UserRepository
) {

    @GetMapping
    fun get(@RequestParam("userId") userId: Int): ResponseEntity<List<Ticket>> {
        val tickets = ticketRepository.findByUserId(userId)
        return ResponseEntity.ok(tickets.map { ticket ->
            Ticket(
                    id = ticket.id,
                    eventId = ticket.eventId,
                    userId = ticket.userId,
                    reservationNumber = ticket.reservationNumber,
            )
        })
    }

    @PostMapping
    suspend fun buyTicket(userId: Int, eventId: Int): ResponseEntity<Ticket> {

        val ticket = ticketRepository.insert(userId, eventId)
        val qrUrl = qrService.generateQrCode("https://bip-backend-b1ew.onrender.com/ticket/validate?ticketId=${ticket!!.id}")
        ticketRepository.addQrUrl(ticket.id, qrUrl)

        val user = userRepository.findUserById(userId)

        emailService.sendVerificationEmail(user!!.email, qrUrl)

        return ResponseEntity.ok(
                Ticket(
                        id = ticket.id,
                        eventId = ticket.eventId,
                        userId = ticket.userId,
                        reservationNumber = "AX3O85HLM",
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

}