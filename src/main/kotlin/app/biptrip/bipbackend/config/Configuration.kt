package app.biptrip.bipbackend.config

import io.netty.resolver.DefaultAddressResolverGroup
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
class Configuration {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder(6)

    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        val httpClient = HttpClient.create()
            .resolver(DefaultAddressResolverGroup.INSTANCE)
        httpClient.warmup().block()
        val webClient = WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .build()

        webClient.get().uri("https://www.google.com").exchangeToMono { it.toEntity(String::class.java) }.block()

        return webClient
    }

}