package hu.balassa.debter.integration

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.configureFor
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import hu.balassa.debter.repository.DebterRepository
import hu.balassa.debter.util.loadJsonFile
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
open class BaseIT {
    @MockBean
    private lateinit var dynamoDb: DynamoDbClient
    @MockBean
    private lateinit var dynamoDbClient: DynamoDbEnhancedClient
    @MockBean
    protected lateinit var repository: DebterRepository
    @Autowired
    protected lateinit var web: WebTestClient

    private val server = WireMockServer(wireMockConfig().port(8088))

    @BeforeEach
    fun setupClass() {
        server.start()
        configureFor("localhost", 8088)
        WireMock.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/api/latest"))
                .withQueryParam("format", WireMock.equalTo("1"))
                .withQueryParam("access_key", WireMock.equalTo("testapikey"))
                .willReturn(WireMock.okJson(loadJsonFile("fixer-response.json")))
        )
    }

    @AfterEach
    fun stopServer() {
        server.stop()
    }
}