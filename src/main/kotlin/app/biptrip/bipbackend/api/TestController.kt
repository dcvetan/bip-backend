package app.biptrip.bipbackend.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class TestController {

    @GetMapping
    fun verifyPurchase(): ResponseEntity<Unit> {
        return ResponseEntity.ok().build()
    }

}