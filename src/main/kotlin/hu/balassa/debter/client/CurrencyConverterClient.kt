package hu.balassa.debter.client

import hu.balassa.debter.model.Currency
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

@Component
open class CurrencyConverterClient {

    fun convert(from: Currency, to: Currency, value: Double): Double {
        return value * loadExchangeRate(from, to)
    }

    @Cacheable("exchangeRates")
    open fun loadExchangeRate(from: Currency, to: Currency): Double {
        return 1.0
    }
}