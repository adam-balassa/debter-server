package hu.balassa.debter.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder

@Configuration
open class ExchangeConfig {
    @Bean(name = ["exchangeWebClient"])
    open fun exchangeClient(@Value("\${fixer.host}") host: String,
                            @Value("\${fixer.api-key}") apiKey: String): WebClient {
        val uri = UriComponentsBuilder.fromUriString(host)
            .path("/api/latest")
            .queryParam("access_key", apiKey)
            .queryParam("format", "1")
            .toUriString()
        return WebClient.create(uri)
    }
}