package app.biptrip.bipbackend.repository

import app.biptrip.bipbackend.jooq.Tables.TICKETS
import app.biptrip.bipbackend.jooq.tables.records.TicketsRecord
import com.sun.java.accessibility.util.EventID
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class TicketRepository(val dslContext: DSLContext) {

    fun findByTicketId(ticketId: Int): TicketsRecord? {
        return dslContext.selectFrom(TICKETS)
            .where(TICKETS.ID.eq(ticketId))
            .fetchOne()
    }

    fun findByUserId(userId: Int): List<TicketsRecord> {
        return dslContext.selectFrom(TICKETS).where(TICKETS.USER_ID.eq(userId)).fetch()
    }

    fun insert(userId: Int, eventID: Int): TicketsRecord? {
        return dslContext.insertInto(TICKETS)
            .set(TICKETS.USER_ID, userId)
            .set(TICKETS.EVENT_ID, eventID)
            .returning() // This will return the entire inserted record
            .fetchOne()
    }

    fun addQrUrl(ticketId: Int, qrUrl: String): Int {
        return dslContext.update(TICKETS)
            .set(TICKETS.QR_URL, qrUrl)
            .where(TICKETS.ID.eq(ticketId))
            .execute()
    }

    fun deleteById(ticketId: Int): Int {
        return dslContext.deleteFrom(TICKETS)
            .where(TICKETS.ID.eq(ticketId))
            .execute()
    }

}