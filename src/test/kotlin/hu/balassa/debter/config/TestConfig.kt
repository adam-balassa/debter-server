package hu.balassa.debter.config

import hu.balassa.debter.repository.DebterRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.test.web.reactive.server.WebTestClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClient


@TestConfiguration
open class TestConfig {
    @MockBean
    private lateinit var dynamoDb: DynamoDbClient
    @MockBean
    private lateinit var dynamoDbClient: DynamoDbEnhancedClient
    @MockBean
    private lateinit var repository: DebterRepository

    @Bean
    open fun webTestClient(): WebTestClient =
        WebTestClient.bindToServer().baseUrl("http://localhost:8080").build()

}