package app.biptrip.bipbackend.api

import app.biptrip.bipbackend.api.model.LoginRequest
import app.biptrip.bipbackend.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder
) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Unit> {

        val user = userRepository.findUserByEmail(loginRequest.email)
                   ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        if (passwordEncoder.matches(loginRequest.password, user.password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        return ResponseEntity.ok().build()

    }

    @PostMapping("/register")
    fun register(@RequestBody loginRequest: LoginRequest): ResponseEntity<Unit> {

        val existingUser = userRepository.findUserByEmail(loginRequest.email)

        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build()
        }

        val newUser = userRepository.createUser(
                email = loginRequest.email,
                password = passwordEncoder.encode(loginRequest.password)
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser)

    }

}