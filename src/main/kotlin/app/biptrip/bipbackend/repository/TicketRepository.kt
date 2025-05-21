package app.biptrip.bipbackend.repository

import app.biptrip.bipbackend.jooq.Tables.TICKETS
import app.biptrip.bipbackend.jooq.tables.records.TicketsRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class TicketRepository(val dslContext: DSLContext) {

    fun findByUserId(userId: Int): List<TicketsRecord> {
        return dslContext.selectFrom(TICKETS).where(TICKETS.USER_ID.eq(userId)).fetch()
    }

    fun insert(ticket: TicketsRecord): Int {
        return dslContext.insertInto(TICKETS)
            .set(ticket)
            .execute()
    }

}