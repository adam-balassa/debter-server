package hu.balassa.debter.integration

import hu.balassa.debter.dto.response.CreateRoomResponse
import hu.balassa.debter.dto.response.RoomDetailsResponse
import hu.balassa.debter.exception.ErrorResponse
import hu.balassa.debter.model.Currency
import hu.balassa.debter.model.Room
import hu.balassa.debter.util.dateOf
import hu.balassa.debter.util.responseBody
import hu.balassa.debter.util.testRoom
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.byLessThan
import org.assertj.core.data.Offset.offset
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit.DAYS
import java.time.temporal.ChronoUnit.MINUTES

class RoomIT: BaseIT() {
    companion object {
        private const val ROOM_KEY = "ROOMID"
    }

    @Test
    fun createRoom() {
        whenever(repository.save(any())).then { (it.arguments[0] as Room).apply { key = ROOM_KEY} }

        val response = web.post().uri("room")
            .bodyValue(object { val roomName = "Test room" })
            .exchange()
            .expectStatus().isCreated
            .responseBody<CreateRoomResponse>()

        assertThat(response).isNotNull
        assertThat(response.key).hasSize(6)
        assertThat(response.defaultCurrency.name).isEqualTo("HUF")
        assertThat(response.rounding).isEqualTo(10.0)

        argumentCaptor<Room> {
            verify(repository).save(capture())
            assertThat(firstValue.key).isEqualTo(response.key)
            assertThat(firstValue.name).isEqualTo("Test room")
            assertThat(firstValue.lastModified).isCloseTo(ZonedDateTime.now(), byLessThan(1, MINUTES))
        }
    }

    @Test
    fun createRoomInvalidRoomName() {
        val response = web.post().uri("room")
            .bodyValue(object { val roomName = "x" })
            .exchange()
            .expectStatus().isBadRequest
            .responseBody<ErrorResponse>()

        assertThat(response.error).isEqualTo("Bad request")
        assertThat(response.reason).contains("roomName")
    }

    @Test
    fun addMembers() {
        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(key = ROOM_KEY))

        web.post().uri("room/$ROOM_KEY/members")
            .bodyValue(object { val members = listOf("test member 1", "test member 2", "test member 3") })
            .exchange()
            .expectStatus().isNoContent

        argumentCaptor<Room> {
            verify(repository).save(capture())
            assertThat(firstValue.key).isEqualTo(ROOM_KEY)
            assertThat(firstValue.name).isEqualTo("Test room")
            assertThat(firstValue.members).extracting<String> { it.name }
                .containsExactly("test member 1", "test member 2", "test member 3")
            assertThat(firstValue.members).allSatisfy {
                assertThat(it.id).isNotNull
                assertThat(it.payments).isEmpty()
                assertThat(it.debts).isEmpty()
            }
        }
    }

    @Test
    fun addMembersNoSuchRoom() {
        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(key = ROOM_KEY))

        val response = web.post().uri("room/INVALIDROOMKEY/members")
            .bodyValue(object { val members = listOf("test member 1", "test member 2", "test member 3") })
            .exchange()
            .expectStatus().isBadRequest
            .responseBody<ErrorResponse>()

        assertThat(response.error).isEqualTo("Bad request")
        assertThat(response.reason).contains("room key")
    }


    @Test
    fun addMembersInvalidMemberNames() {
        val response = web.post().uri("room/$ROOM_KEY/members")
            .bodyValue(object { val members = listOf("test member", "test member 2", "test member") })
            .exchange()
            .expectStatus().isBadRequest
            .responseBody<ErrorResponse>()

        assertThat(response.error).isEqualTo("Bad request")
        assertThat(response.reason).contains("members")
    }

    @Test
    fun getRoom() {
        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(ROOM_KEY))

        val response = web.get().uri("room/$ROOM_KEY")
            .exchange()
            .expectStatus().isOk
            .responseBody<RoomDetailsResponse>()

        assertThat(response).isNotNull

        assertThat(response.members).extracting<String> { it.id }.containsExactly("member1", "member2")
        assertThat(response.members).extracting<String> { it.name }.containsExactly("test member 1", "test member 2")

        assertThat(response.payments).allSatisfy {
            assertThat(it.active).isTrue
            assertThat(it.date).isCloseTo(dateOf(2020, 9, 1), byLessThan(1, DAYS))
            assertThat(it.currency).isEqualTo(Currency.HUF)
            assertThat(it.included).hasSize(2)
            assertThat(it.note).isEqualTo("test note")
            assertThat(it.realValue).isCloseTo(20.0, offset(.0001))
            assertThat(it.value).isCloseTo(20.0, offset(.0001))
        }
        assertThat(response.payments).extracting<String> { it.id }
            .containsExactlyInAnyOrder("member1payment1", "member1payment2", "member2payment1", "member2payment2")
        assertThat(response.payments).extracting<String> { it.memberId }
            .containsExactlyInAnyOrder("member1", "member1", "member2", "member2")

        assertThat(response.debts).allSatisfy {
            assertThat(it.currency).isEqualTo(Currency.HUF)
            assertThat(it.value).isCloseTo(20.0, offset(.0001))
            assertThat(it.arranged).isFalse
        }
        assertThat(response.debts).extracting<Pair<String, String>> { it.from to it.to  }
            .containsExactlyInAnyOrder("member1" to "member2", "member2" to "member1")
    }
}