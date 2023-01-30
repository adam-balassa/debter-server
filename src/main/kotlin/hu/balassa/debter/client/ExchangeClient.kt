package hu.balassa.debter.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hu.balassa.debter.model.Currency
import hu.balassa.debter.model.Currency.EUR
import org.apache.http.client.utils.URIBuilder
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodySubscribers.mapping
import java.net.http.HttpResponse.BodySubscribers.ofInputStream

open class ExchangeClient (
    private val baseUrl: String,
    private val apiKey: String
) {
    private val exchangeRates = lazy<ExchangeRates> {
        val uri = URIBuilder().apply {
            host = baseUrl
            path = "/api/latest"
            addParameter("access_key", apiKey)
            addParameter("format", "1")
        }.toString()
        HttpRequest.newBuilder(URI(uri)).GET().build().let { request ->
            HttpClient.newHttpClient().send(request) {
                mapping(ofInputStream()) {
                    jacksonObjectMapper().readValue(it, ExchangeRates::class.java)
                }
            }.body()
        }
    }

    fun convert(from: Currency, to: Currency, value: Double): Double {
        return value * loadExchangeRate(from, to)
    }

    private fun loadExchangeRate(from: Currency, to: Currency): Double {
        val rates = exchangeRates.value.rates
        return when {
            from == EUR -> rates[to.name]!!
            to == EUR -> 1 / rates[from.name]!!
            else -> rates[to.name]!! / rates[from.name]!!
        }
    }

    data class ExchangeRates(val rates: Map<String, Double>)
}