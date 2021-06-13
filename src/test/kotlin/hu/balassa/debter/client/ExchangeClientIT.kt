package hu.balassa.debter.client

import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.verify
import hu.balassa.debter.integration.BaseIT
import hu.balassa.debter.model.Currency.EUR
import hu.balassa.debter.model.Currency.HUF
import hu.balassa.debter.model.Currency.USD
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class ExchangeClientIT: BaseIT()  {
    @Autowired
    private lateinit var exchangeClient: ExchangeClient

    @Test
    fun loadExchangeRatesEurToHuf() {
        val result = exchangeClient.convert(EUR, HUF, 10.0)

        assertThat(result).isCloseTo(3481.8, Offset.offset(0.1))
        verify(getRequestedFor(urlPathEqualTo("/api/latest"))
            .withQueryParam("access_key", equalTo("testapikey"))
            .withQueryParam("format", equalTo("1")))
    }

    @Test
    fun loadExchangeRatesHufToEur() {
        val result = exchangeClient.convert(HUF, EUR, 1000.0)

        assertThat(result).isCloseTo(2.87, Offset.offset(0.1))
        verify(getRequestedFor(urlPathEqualTo("/api/latest"))
            .withQueryParam("access_key", equalTo("testapikey"))
            .withQueryParam("format", equalTo("1")))
    }

    @Test
    fun loadExchangeRatesUsdToHuf() {
        val result = exchangeClient.convert(USD, HUF, 12.0)

        assertThat(result).isCloseTo(3450.45, Offset.offset(0.1))
        verify(getRequestedFor(urlPathEqualTo("/api/latest"))
            .withQueryParam("access_key", equalTo("testapikey"))
            .withQueryParam("format", equalTo("1")))
    }
}