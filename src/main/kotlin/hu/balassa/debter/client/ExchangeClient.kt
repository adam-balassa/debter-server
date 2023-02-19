package hu.balassa.debter.client

import hu.balassa.debter.handler.objectMapper
import hu.balassa.debter.model.Currency
import hu.balassa.debter.model.Currency.EUR
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit.SECONDS


open class ExchangeClient (
    private val baseUrl: String,
    private val apiKey: String
) {
    private val httpClient = OkHttpClient.Builder().connectTimeout(5, SECONDS) .build()
    private val exchangeRates = lazy {
        val url = HttpUrl.Builder()
            .scheme("http")
            .host(baseUrl)
            .addEncodedPathSegments("api/latest")
            .addQueryParameter("access_key", apiKey)
            .addQueryParameter("format", "1")
            .build()
        val request = Request.Builder().url(url).get().build()
        val response = httpClient.newCall(request).execute()
        check(response.isSuccessful) { "Failed to load conversion rates" }
        objectMapper().readValue(response.body().byteStream(), ExchangeRates::class.java)
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