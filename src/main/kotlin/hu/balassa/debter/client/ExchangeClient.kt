package hu.balassa.debter.client

import hu.balassa.debter.model.Currency
import hu.balassa.debter.model.Currency.EUR
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
open class ExchangeClient (
    private val webClient: WebClient
) {

    fun convert(from: Currency, to: Currency, value: Double): Double {
        return value * loadExchangeRate(from, to)
    }

    private fun loadExchangeRate(from: Currency, to: Currency): Double {
        val rates = loadAllExchangeRates().rates
        return when {
            from == EUR -> rates[to.name]!!
            to == EUR -> 1 / rates[from.name]!!
            else -> rates[to.name]!! / rates[from.name]!!
        }
    }

    @Cacheable("exchangeRates")
    open fun loadAllExchangeRates(): ExchangeRates = webClient.get().retrieve()
        .bodyToFlux(ExchangeRates::class.java).blockFirst()
        ?: throw Exception("Unsuccessful query")

    data class ExchangeRates(val rates: Map<String, Double>)
}