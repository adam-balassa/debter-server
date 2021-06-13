package hu.balassa.debter.client

import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.exactly
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.verify
import hu.balassa.debter.integration.BaseIT
import hu.balassa.debter.model.Currency
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CacheConfigTest: BaseIT() {

    @Autowired
    private lateinit var exchangeClient: ExchangeClient

    @Test
    fun loadExchangeRatesEurToHuf() {
        exchangeClient.convert(Currency.EUR, Currency.HUF, 10.0)
        exchangeClient.convert(Currency.HUF, Currency.EUR, 1000.0)
        exchangeClient.convert(Currency.USD, Currency.EUR, 6.0)

        verify(
            exactly(1), getRequestedFor(urlPathEqualTo("/api/latest"))
                .withQueryParam("access_key", equalTo("testapikey"))
                .withQueryParam("format", equalTo("1")))
    }
}