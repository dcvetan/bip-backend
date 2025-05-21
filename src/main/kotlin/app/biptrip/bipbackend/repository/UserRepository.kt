package app.biptrip.bipbackend.repository

import app.biptrip.bipbackend.jooq.Tables.USERS
import app.biptrip.bipbackend.jooq.tables.records.UsersRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class UserRepository(val dslContext: DSLContext) {

    fun createUser(email: String, password: String) {
        dslContext.insertInto(USERS)
            .columns(USERS.EMAIL, USERS.PASSWORD)
            .values(email, password)
            .execute()
    }

    fun findUserByEmail(email: String): UsersRecord? {
        return dslContext.selectFrom(USERS)
            .where(USERS.EMAIL.eq(email))
            .fetchOne()
    }

}