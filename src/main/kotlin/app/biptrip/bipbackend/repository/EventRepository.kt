package app.biptrip.bipbackend.repository

import app.biptrip.bipbackend.jooq.Tables.EVENTS
import app.biptrip.bipbackend.jooq.tables.records.EventsRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class EventRepository(val dslContext: DSLContext) {

    fun findAll(): List<EventsRecord> {
        return dslContext.selectFrom(EVENTS).fetch()
    }

    fun insert(event: EventsRecord): Int {
        return dslContext.insertInto(EVENTS)
            .set(event)
            .execute()
    }

    fun findById(id: Int): EventsRecord? {
        return dslContext.selectFrom(EVENTS)
            .where(EVENTS.ID.eq(id))
            .fetchOne()
    }

    fun decrementTickets(eventId: Int): Int {
        return dslContext.update(EVENTS)
            .set(EVENTS.TICKETS_AVAILABLE, EVENTS.TICKETS_AVAILABLE - 1)
            .where(EVENTS.ID.eq(eventId))
            .execute()
    }

}