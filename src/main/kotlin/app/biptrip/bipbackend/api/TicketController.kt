package app.biptrip.bipbackend.api

import app.biptrip.bipbackend.api.model.Ticket
import app.biptrip.bipbackend.repository.TicketRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/ticket")
class TicketController(
        private val ticketRepository: TicketRepository
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

}