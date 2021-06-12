package hu.balassa.debter.util

import org.springframework.test.web.reactive.server.WebTestClient


inline fun <reified T > WebTestClient.ResponseSpec.responseBody() =
    expectBody(T::class.java).returnResult().responseBody!!
