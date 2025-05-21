package app.biptrip.bipbackend.api

import app.biptrip.bipbackend.api.model.Event
import app.biptrip.bipbackend.repository.EventRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/event")
class EventController(
        private val eventRepository: EventRepository
) {

    @GetMapping
    fun get(): ResponseEntity<List<Event>> {
        val events = eventRepository.findAll()
        return ResponseEntity.ok(events.map { event ->
            Event(
                    id = event.id,
                    title = event.title,
                    description = event.description,
                    startTime = event.startTime,
                    endTime = event.endTime,
                    location = event.location,
                    price = event.price,
                    imageUrl = event.imageUrl,
                    ticketsAvailable = event.ticketsAvailable,
            )
        })
    }

}