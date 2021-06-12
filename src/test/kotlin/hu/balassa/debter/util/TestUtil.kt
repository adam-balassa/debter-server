package hu.balassa.debter.util

import hu.balassa.debter.model.Currency
import hu.balassa.debter.model.Room
import org.springframework.test.web.reactive.server.WebTestClient

fun testRoom(
    id: String? = null,
    key: String,
    name: String = "Test room",
    currency: Currency = Currency.HUF,
    rounding: Double = 10.0
) = Room().also {
    it.id = id
    it.key = key
    it.name = name
    it.currency = currency
    it.rounding = rounding
}

inline fun <reified T> WebTestClient.ResponseSpec.responseBody() =
    expectBody(T::class.java).returnResult().responseBody!!
