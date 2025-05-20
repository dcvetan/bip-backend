package app.biptrip.bipbackend.api

import app.bip_backend.generated.jooq.Tables
import app.bip_backend.generated.jooq.tables.records.UsersRecord
import org.jooq.DSLContext
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class TestController(
        private val dslContext: DSLContext
) {

    @GetMapping
    fun verifyPurchase(): ResponseEntity<UsersRecord> {

        dslContext.insertInto(Tables.USERS)
            .set(Tables.USERS.EMAIL, "test")
            .set(Tables.USERS.PASSWORD, "test")
            .execute()

        return ResponseEntity.ok(dslContext.selectFrom(Tables.USERS).fetchOne())
    }

}