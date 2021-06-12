package hu.balassa.debter.integration

import hu.balassa.debter.config.TestConfig
import hu.balassa.debter.dto.response.CreateRoomResponse
import hu.balassa.debter.exception.ErrorResponse
import hu.balassa.debter.model.Member
import hu.balassa.debter.model.Room
import hu.balassa.debter.repository.DebterRepository
import hu.balassa.debter.util.responseBody
import hu.balassa.debter.util.testRoom
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient

@Import(TestConfig::class)
@SpringBootTest( webEnvironment = DEFINED_PORT )
class RoomIT {
    companion object {
        private const val ROOM_KEY = "ROOMID"
    }

    @Autowired
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

    @Test
    fun createRoomInvalidRoomName() {
        val response = web.post().uri("room")
            .bodyValue(object { val name = "x" })
            .exchange()
            .expectStatus().isBadRequest
            .responseBody<ErrorResponse>()

        assertThat(response.error).isEqualTo("Bad request")
        assertThat(response.reason).contains("name")
    }

    @Test
    fun addMembers() {
        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(key = ROOM_KEY))

        web.post().uri("room/$ROOM_KEY/members")
            .bodyValue(object { val memberNames = listOf("test member 1", "test member 2", "test member 3") })
            .exchange()
            .expectStatus().isNoContent

        argumentCaptor<Room> {
            verify(repository).save(capture())
            assertThat(firstValue.id).isNull()
            assertThat(firstValue.key).isEqualTo(ROOM_KEY)
            assertThat(firstValue.name).isEqualTo("Test room")
            assertThat(firstValue.members).extracting<String> { it.name }
                .containsExactly("test member 1", "test member 2", "test member 3")
            assertThat(firstValue.members).allSatisfy {
                assertThat(it.id).isNotNull
                assertThat(it.debt).isEqualTo(0.0)
                assertThat(it.sum).isEqualTo(0.0)
            }
        }
    }

    @Test
    fun addMembersNoSuchRoom() {
        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(key = ROOM_KEY))

        val response = web.post().uri("room/INVALIDROOMKEY/members")
            .bodyValue(object { val memberNames = listOf("test member 1", "test member 2", "test member 3") })
            .exchange()
            .expectStatus().isBadRequest
            .responseBody<ErrorResponse>()

        assertThat(response.error).isEqualTo("Bad request")
        assertThat(response.reason).contains("room key")
    }


    @Test
    fun addMembersInvalidMemberNames() {
        val response = web.post().uri("room/$ROOM_KEY/members")
            .bodyValue(object { val memberNames = listOf("test member", "test member 2", "test member") })
            .exchange()
            .expectStatus().isBadRequest
            .responseBody<ErrorResponse>()

        assertThat(response.error).isEqualTo("Bad request")
        assertThat(response.reason).contains("memberNames")
    }

}