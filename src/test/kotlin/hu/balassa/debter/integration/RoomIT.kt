//package hu.balassa.debter.integration
//
//import hu.balassa.debter.dto.response.CreateRoomResponse
//import hu.balassa.debter.dto.response.MemberResponse
//import hu.balassa.debter.dto.response.RoomDetailsResponse
//import hu.balassa.debter.dto.response.RoomSettings
//import hu.balassa.debter.dto.response.RoomSummary
//import hu.balassa.debter.exception.ErrorResponse
//import hu.balassa.debter.model.Currency
//import hu.balassa.debter.model.Currency.EUR
//import hu.balassa.debter.model.Currency.HUF
//import hu.balassa.debter.model.Room
//import hu.balassa.debter.util.dateOf
//import hu.balassa.debter.util.responseBody
//import hu.balassa.debter.util.responseBodyList
//import hu.balassa.debter.util.testMember
//import hu.balassa.debter.util.testPayment
//import hu.balassa.debter.util.testRoom
import hu.balassa.debter.dto.response.RoomSummary
import hu.balassa.debter.integration.BaseIT
import hu.balassa.debter.model.Currency
import hu.balassa.debter.util.testRoom
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.Mockito

//import org.assertj.core.api.Assertions.byLessThan
//import org.assertj.core.api.Assertions.within
//import org.assertj.core.data.Offset.offset
//import org.junit.jupiter.api.Test
//import org.mockito.kotlin.any
//import org.mockito.kotlin.argumentCaptor
//import org.mockito.kotlin.verify
//import java.time.ZonedDateTime
//import java.time.temporal.ChronoUnit.DAYS
//import java.time.temporal.ChronoUnit.MINUTES
//
class RoomIT: BaseIT() {
    companion object {
        private const val ROOM_KEY = "ROOMID"
    }

//    @Test
//    fun createRoom() {
//        whenever(repository.save(any())).then { (it.arguments[0] as Room).apply { key = ROOM_KEY} }
//
//        val response = web.post().uri("room")
//            .bodyValue(object { val name = "Test room" })
//            .exchange()
//            .expectStatus().isCreated
//            .responseBody<CreateRoomResponse>()
//
//        assertThat(response).isNotNull
//        assertThat(response.roomKey).hasSize(6)
//        assertThat(response.currency.name).isEqualTo("HUF")
//        assertThat(response.rounding).isEqualTo(10.0)
//
//        argumentCaptor<Room> {
//            verify(repository).save(capture())
//            assertThat(firstValue.key).isEqualTo(response.roomKey)
//            assertThat(firstValue.name).isEqualTo("Test room")
//            assertThat(firstValue.lastModified).isCloseTo(ZonedDateTime.now(), byLessThan(1, MINUTES))
//        }
//    }
//
//    @Test
//    fun createRoomInvalidRoomName() {
//        val response = web.post().uri("room")
//            .bodyValue(object { val name = "x" })
//            .exchange()
//            .expectStatus().isBadRequest
//            .responseBody<ErrorResponse>()
//
//        assertThat(response.error).isEqualTo("Bad request")
//        assertThat(response.reason).contains("name")
//    }
//
//    @Test
//    fun addMembers() {
//        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(key = ROOM_KEY))
//
//        web.post().uri("room/$ROOM_KEY/members")
//            .bodyValue(object { val members = listOf("test member 1", "test member 2", "test member 3") })
//            .exchange()
//            .expectStatus().isNoContent
//            .expectBody().isEmpty
//
//        argumentCaptor<Room> {
//            verify(repository).save(capture())
//            assertThat(firstValue.key).isEqualTo(ROOM_KEY)
//            assertThat(firstValue.name).isEqualTo("Test room")
//            assertThat(firstValue.members).extracting<String> { it.name }
//                .containsExactly("test member 1", "test member 2", "test member 3")
//            assertThat(firstValue.members).allSatisfy {
//                assertThat(it.id).isNotNull
//                assertThat(it.payments).isEmpty()
//                assertThat(it.debts).isEmpty()
//            }
//        }
//    }
//
//    @Test
//    fun addMembersNoSuchRoom() {
//        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(key = ROOM_KEY))
//
//        val response = web.post().uri("room/INVALIDROOMKEY/members")
//            .bodyValue(object { val members = listOf("test member 1", "test member 2", "test member 3") })
//            .exchange()
//            .expectStatus().isBadRequest
//            .responseBody<ErrorResponse>()
//
//        assertThat(response.error).isEqualTo("Bad request")
//        assertThat(response.reason).contains("room key")
//    }
//
//
//    @Test
//    fun addMembersInvalidMemberNames() {
//        val response = web.post().uri("room/$ROOM_KEY/members")
//            .bodyValue(object { val members = listOf("test member", "test member 2", "test member") })
//            .exchange()
//            .expectStatus().isBadRequest
//            .responseBody<ErrorResponse>()
//
//        assertThat(response.error).isEqualTo("Bad request")
//        assertThat(response.reason).contains("members")
//    }
//
//    @Test
//    fun getRoom() {
//        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(ROOM_KEY))
//
//        val response = web.get().uri("room/$ROOM_KEY")
//            .exchange()
//            .expectStatus().isOk
//            .responseBody<RoomDetailsResponse>()
//
//        assertThat(response).isNotNull
//
//        assertThat(response.members).extracting<String> { it.id }.containsExactly("member1", "member2")
//        assertThat(response.members).extracting<String> { it.name }.containsExactly("test member 1", "test member 2")
//
//        assertThat(response.payments).allSatisfy {
//            assertThat(it.active).isTrue
//            assertThat(it.date).isCloseTo(dateOf(2020, 9, 1), byLessThan(1, DAYS))
//            assertThat(it.currency).isEqualTo(HUF)
//            assertThat(it.included).hasSize(2)
//            assertThat(it.note).isEqualTo("test note")
//            assertThat(it.realValue).isCloseTo(20.0, offset(.0001))
//            assertThat(it.value).isCloseTo(20.0, offset(.0001))
//        }
//        assertThat(response.payments).extracting<String> { it.id }
//            .containsExactlyInAnyOrder("member1payment1", "member1payment2", "member2payment1", "member2payment2")
//        assertThat(response.payments).extracting<String> { it.memberId }
//            .containsExactlyInAnyOrder("member1", "member1", "member2", "member2")
//
//        assertThat(response.debts).allSatisfy {
//            assertThat(it.currency).isEqualTo(HUF)
//            assertThat(it.value).isCloseTo(20.0, offset(.0001))
//            assertThat(it.arranged).isFalse
//        }
//        assertThat(response.debts).extracting<Pair<String, String>> { it.from to it.to  }
//            .containsExactlyInAnyOrder("member1" to "member2", "member2" to "member1")
//    }
//
    @Test @Disabled
    fun getRoomSummary() {
        Mockito.`when`(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(ROOM_KEY))

        val response = web.get()
            .pattern("room/{roomKey}/summary")
            .pathParam("roomKey", ROOM_KEY)
            .exchange()
            .expectStatus(200)
            .responseBody<RoomSummary>()

        assertThat(response.currency).isEqualTo(Currency.HUF)
        assertThat(response.sum).isEqualTo(80.0)
        assertThat(response.roomKey).isEqualTo(ROOM_KEY)
        assertThat(response.name).isEqualTo("Test room")
        assertThat(response.memberSummary).hasSize(2)
            .allSatisfy {
                assertThat(it.sum).isEqualTo(40.0)
                assertThat(it.debt).isEqualTo(20.0)
            }
            .extracting<String> { it.name }.containsExactly("test member 1", "test member 2")
    }
}
//
//    @Test
//    fun getMembers() {
//        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(ROOM_KEY))
//
//        val response = web.get().uri("room/$ROOM_KEY/members")
//            .exchange()
//            .expectStatus().isOk
//            .responseBodyList<MemberResponse>()
//
//        assertThat(response).hasSize(2)
//            .anySatisfy {
//                assertThat(it.id).isEqualTo("member1")
//                assertThat(it.name).isEqualTo("test member 1")
//            }.anySatisfy {
//                assertThat(it.id).isEqualTo("member2")
//                assertThat(it.name).isEqualTo("test member 2")
//            }
//    }
//
//    @Test
//    fun getRoomSettings() {
//        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(ROOM_KEY))
//
//        val response = web.get().uri("room/$ROOM_KEY/settings")
//            .exchange()
//            .expectStatus().isOk
//            .responseBody<RoomSettings>()
//
//        assertThat(response.rounding).isEqualTo(10.0)
//        assertThat(response.currency).isEqualTo(HUF)
//    }
//
//    @Test
//    fun updateRoomSettings() {
//        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(ROOM_KEY))
//
//        web.put().uri("room/$ROOM_KEY/settings")
//            .bodyValue(object {
//                val rounding = 100.0
//                val currency = "EUR"
//            })
//            .exchange()
//            .expectStatus().isNoContent
//            .expectBody().isEmpty
//
//        argumentCaptor<Room> {
//            verify(repository).save(capture())
//            assertThat(firstValue.rounding).isEqualTo(100.0)
//            assertThat(firstValue.currency).isEqualTo(EUR)
//            assertThat(firstValue.members.flatMap { it.payments }).allSatisfy {
//                assertThat(it.convertedValue).isCloseTo(20.0 / 348.177726, within(0.000001))
//            }
//        }
//    }
//
//    @Test
//    fun addMemberToExistingRoom() {
//        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(ROOM_KEY))
//
//        web.put().uri("room/$ROOM_KEY/members")
//            .bodyValue(object {
//                val name = "new member"
//                val includedPaymentIds = listOf("member1payment1", "member2payment2")
//            })
//            .exchange()
//            .expectStatus().isNoContent
//            .expectBody().isEmpty
//
//        argumentCaptor<Room> {
//            verify(repository).save(capture())
//            var id: String? = null
//            assertThat(firstValue.members).hasSize(3)
//                .anySatisfy {
//                    assertThat(it.name).isEqualTo("new member")
//                    assertThat(it.payments).isEmpty()
//                    assertThat(it.debts).isNotEmpty
//                    id = it.id
//                }.anyMatch {
//                    it.payments.any { payment -> payment.id == "member1payment1" && id in payment.includedMemberIds }
//                }.anyMatch {
//                    it.payments.any { payment -> payment.id == "member2payment2" && id in payment.includedMemberIds }
//                }
//        }
//    }
//
//    @Test
//    fun deleteMember() {
//        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(ROOM_KEY, members = listOf(
//            testMember(id = "1", payments = listOf(testPayment(value = 300.0, includedMemberIds = listOf("1", "2")))),
//            testMember(id = "2", payments = listOf(testPayment(value = 400.0, includedMemberIds = listOf("1"), active = false))),
//            testMember(id = "3", payments = emptyList()),
//        )))
//
//        web.delete().uri("room/$ROOM_KEY/members/3")
//            .exchange()
//            .expectStatus().isNoContent
//            .expectBody().isEmpty
//
//        argumentCaptor<Room> {
//            verify(repository).save(capture())
//            assertThat(firstValue.members).hasSize(2)
//                .noneMatch { it.id == "3" }
//        }
//    }
//
//    @Test
//    fun deleteIncludedMember() {
//        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(ROOM_KEY, members = listOf(
//            testMember(id = "1", payments = listOf(testPayment(value = 300.0, includedMemberIds = listOf("1", "2")))),
//            testMember(id = "2", payments = listOf(testPayment(value = 400.0, includedMemberIds = listOf("1", "3")))),
//            testMember(id = "3", payments = emptyList()),
//        )))
//
//        web.delete().uri("room/$ROOM_KEY/members/3")
//            .exchange()
//            .expectStatus().isBadRequest
//    }
//
//    @Test
//    fun deleteMemberWithPayment() {
//        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(ROOM_KEY, members = listOf(
//            testMember(id = "1", payments = listOf(testPayment(value = 300.0, includedMemberIds = listOf("1", "2")))),
//            testMember(id = "2", payments = listOf(testPayment(value = 400.0, includedMemberIds = listOf("1"), active = false))),
//            testMember(id = "3", payments = listOf(testPayment(value = 300.0, includedMemberIds = listOf("1", "2")))),
//        )))
//
//        web.delete().uri("room/$ROOM_KEY/members/3")
//            .exchange()
//            .expectStatus().isBadRequest
//    }
//}