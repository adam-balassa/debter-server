package hu.balassa.debter.integration

import hu.balassa.debter.client.ExchangeClient
import hu.balassa.debter.config.DynamoDbConfig
import hu.balassa.debter.handler.Application
import hu.balassa.debter.handler.Handler
import hu.balassa.debter.repository.DebterRepository
import hu.balassa.debter.util.WebTestClient
import hu.balassa.debter.util.loadJsonFile
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy

open class BaseIT {
    protected val repository = mock<DebterRepository>()
    private val exchangeClient = spy<ExchangeClient> {
        on { requestExchangeRates() } doReturn loadJsonFile("fixer-response.json")
    }
    private val app = spy<Application> {
//        on { try { dbConfig() } catch (e: Exception) {} } doReturn mock<DynamoDbConfig>()
        on { debterRepository(any()) } doReturn repository
        on { exchangeClient() } doReturn exchangeClient
    }
    protected val web = WebTestClient(Handler().also { it.app = app })
}