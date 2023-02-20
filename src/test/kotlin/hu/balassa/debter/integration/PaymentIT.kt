package hu.balassa.debter.integration

import hu.balassa.debter.dto.response.GetPaymentsResponse
import hu.balassa.debter.model.Currency.HUF
import hu.balassa.debter.model.Room
import hu.balassa.debter.util.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.byLessThan
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.MINUTES

class PaymentIT: BaseIT() {
    companion object {
        private const val ROOM_KEY = "ROOMID"
        private const val PAYMENT_ID = "member1payment1"
    }

    @Test
    fun addPayment() {
        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(ROOM_KEY))

        web.post().pattern("room/{roomKey}/payments").pathParam("roomKey", ROOM_KEY)
            .bodyValue(object {
                val value = 10.0
                val memberId = "member1"
                val currency = "HUF"
                val note = "test note"
                val date = "2020-09-12T12:30:00+02:00"
                val included = listOf("member1", "member2")
            })
            .exchange()
            .expectStatus().isCreated

        argumentCaptor<Room> {
            verify(repository).save(capture())
            assertThat(firstValue.key).isEqualTo(ROOM_KEY)
            assertThat(firstValue.members.find { it.id == "member1" }!!.payments).hasSize(3).anySatisfy {
                assertThat(it.value).isEqualTo(10.0)
                assertThat(it.convertedValue).isEqualTo(10.0)
                assertThat(it.currency).isEqualTo(HUF)
                assertThat(it.date).isCloseTo(dateOf(2020, 9, 12, 12, 30), byLessThan(1, MINUTES))
                assertThat(it.includedMemberIds).containsExactly("member1", "member2")
                assertThat(it.note).isEqualTo("test note")
                assertThat(it.active).isTrue
            }
        }
    }

    @Test
    fun addPaymentWithForeignCurrency() {
        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(ROOM_KEY))

        web.post().pattern("room/{roomKey}/payments").pathParam("roomKey", ROOM_KEY)
            .bodyValue(object {
                val value = 10.0
                val memberId = "member1"
                val currency = "EUR"
                val note = "test note"
                val date = "2020-09-12T12:30:00+02:00"
                val included = listOf("member1", "member2")
            })
            .exchange()
            .expectStatus().isCreated

        argumentCaptor<Room> {
            verify(repository).save(capture())
            assertThat(firstValue.key).isEqualTo(ROOM_KEY)
            assertThat(firstValue.members.find { it.id == "member1" }!!.payments).hasSize(3).anySatisfy {
                assertThat(it.value).isEqualTo(10.0)
                assertThat(it.convertedValue).isCloseTo(3481.8, Offset.offset(0.1))
            }
        }
    }

    @Test
    fun addPaymentResolveDebt() {
        val room = testRoom(rounding = 10.0, members = listOf(
            testMember(id = "member1", debts = listOf(testDebt(value = 100.0, payeeId = "member2"))),
            testMember(id = "member2", debts = emptyList())))
        whenever(repository.findByKey(ROOM_KEY)).thenReturn(room)

         web.post().pattern("room/{roomKey}/payments").pathParam("roomKey", ROOM_KEY)
            .bodyValue(object {
                val value = 97.0
                val memberId = "member1"
                val currency = "HUF"
                val note = "test note"
                val date = "2020-09-12T12:30:00+02:00"
                val included = listOf("member2")
            })
            .exchange()
            .expectStatus().isCreated

        argumentCaptor<Room> {
            verify(repository).save(capture())
            assertThat(firstValue.members[0].debts).allMatch { it.arranged }
        }
    }

    @Test
    fun getPayments() {
        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(ROOM_KEY, members = listOf(
            testMember(id = "1", payments = listOf(testPayment(value = 300.0, includedMemberIds = listOf("1", "2")))),
            testMember(id = "2", payments = listOf(testPayment(value = 400.0, includedMemberIds = listOf("1"), active = false))),
        )))

        val response = web.get().pattern("room/{roomKey}/payments").pathParam("roomKey", ROOM_KEY)
            .exchange()
            .expectStatus().isOk
            .responseBody<GetPaymentsResponse>()

        assertThat(response.activePayments).hasSize(1).allSatisfy {
            assertThat(it.currency).isEqualTo(HUF)
            assertThat(it.date).isCloseTo(dateOf(2020, 9, 1), byLessThan(1, ChronoUnit.DAYS))
            assertThat(it.includedMembers).extracting<String> { it.memberName }.containsExactly("test member 1", "test member 2")
            assertThat(it.includedMembers).extracting<Boolean> { it.included }.containsExactly(true, true)
            assertThat(it.note).isEqualTo("test note")
            assertThat(it.value).isCloseTo(300.0, Offset.offset(.0001))
            assertThat(it.convertedValue).isCloseTo(20.0, Offset.offset(.0001))

        }
        assertThat(response.deletedPayments).hasSize(1).allSatisfy {
            assertThat(it.currency).isEqualTo(HUF)
            assertThat(it.date).isCloseTo(dateOf(2020, 9, 1), byLessThan(1, ChronoUnit.DAYS))
            assertThat(it.includedMembers).extracting<String> { it.memberName }.containsExactly("test member 1", "test member 2")
            assertThat(it.includedMembers).extracting<Boolean> { it.included }.containsExactly(true, false)
            assertThat(it.note).isEqualTo("test note")
            assertThat(it.value).isCloseTo(400.0, Offset.offset(.0001))
            assertThat(it.convertedValue).isCloseTo(20.0, Offset.offset(.0001))
        }
    }


    @Test
    fun deletePayment() {
        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(ROOM_KEY))

        web.delete().pattern("room/{roomKey}/payments/{paymentId}")
            .pathParam("roomKey", ROOM_KEY).pathParam("paymentId", PAYMENT_ID)
            .exchange()
            .expectStatus().isNoContent

        argumentCaptor<Room> {
            verify(repository).save(capture())
            assertThat(firstValue.members.find { it.id == "member1" }!!.payments).anySatisfy {
                assertThat(it.id).isEqualTo(PAYMENT_ID)
                assertThat(it.active).isFalse
            }
        }
    }

    @Test
    fun revivePayment() {
        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(
            key = ROOM_KEY,
            members = listOf(testMember(id="member1", payments = listOf(testPayment("member1payment1", active = false))))
        ))

         web.patch().pattern("room/{roomKey}/payments/{paymentId}")
             .pathParam("roomKey", ROOM_KEY).pathParam("paymentId", PAYMENT_ID)
            .exchange()
            .expectStatus().isNoContent

        argumentCaptor<Room> {
            verify(repository).save(capture())
            assertThat(firstValue.members.find { it.id == "member1" }!!.payments).anySatisfy {
                assertThat(it.id).isEqualTo(PAYMENT_ID)
                assertThat(it.active).isTrue
            }
        }
    }
}