package hu.balassa.debter.integration

import hu.balassa.debter.config.TestConfig
import hu.balassa.debter.dto.response.CreateRoomResponse
import hu.balassa.debter.model.Room
import hu.balassa.debter.repository.DebterRepository
import hu.balassa.debter.util.responseBody
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient

@Import(TestConfig::class)
@SpringBootTest( webEnvironment = DEFINED_PORT )
class RoomIT {
    @MockBean
    private lateinit var repository: DebterRepository

    @Autowired
    private lateinit var web: WebTestClient

    @Test
    fun createRoom() {
        val response = web.post().uri("room")
            .bodyValue(object { val name = "Test room" })
            .exchange()
            .expectStatus().isCreated
            .responseBody<CreateRoomResponse>()

        assertThat(response).isNotNull
        assertThat(response.roomKey).hasSize(6)
        assertThat(response.defaultCurrency.name).isEqualTo("HUF")
        assertThat(response.rounding).isEqualTo(10.0)

        argumentCaptor<Room> {
            verify(repository).save(capture())
            assertThat(firstValue.id).isNull()
            assertThat(firstValue.key).isEqualTo(response.roomKey)
            assertThat(firstValue.name).isEqualTo("Test room") }
    }
}