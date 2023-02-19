//package hu.balassa.debter.integration
//
//import hu.balassa.debter.dto.response.GetDebtsResponse
//import hu.balassa.debter.dto.response.GetPaymentsResponse
//import hu.balassa.debter.model.Currency
//import hu.balassa.debter.model.Currency.HUF
//import hu.balassa.debter.util.responseBody
//import hu.balassa.debter.util.testDebt
//import hu.balassa.debter.util.testMember
//import hu.balassa.debter.util.testRoom
//import org.assertj.core.api.Assertions.assertThat
//import org.junit.jupiter.api.Test
//import org.mockito.kotlin.whenever
//
//class DebtIT : BaseIT() {
//    companion object {
//        private const val ROOM_KEY = "ROOMID"
//    }
//
//    @Test
//    fun getDebts() {
//        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(ROOM_KEY, members = listOf(
//            testMember(id = "1", debts= listOf(testDebt(payeeId = "2", value = 300.0), testDebt(payeeId = "3", value = 200.0))),
//            testMember(id = "2", debts= listOf(testDebt(payeeId = "3", value = 100.0, arranged = true))),
//            testMember(id = "3", debts = emptyList()),
//        )))
//
//        val response = web.get().uri("room/${ROOM_KEY}/debts")
//            .exchange()
//            .expectStatus().isOk
//            .responseBody<GetDebtsResponse>()
//
//        assertThat(response.currency).isEqualTo(HUF)
//        assertThat(response.debts).hasSize(2)
//            .anySatisfy { member ->
//                assertThat(member.id).isEqualTo("1")
//                assertThat(member.name).isEqualTo("test member 1")
//                assertThat(member.debt).isEqualTo(500.0)
//                assertThat(member.sum).isEqualTo(40.0)
//                assertThat(member.debts).hasSize(2)
//                    .anySatisfy {
//                        assertThat(it.payeeId).isEqualTo("2")
//                        assertThat(it.payeeName).isEqualTo("test member 2")
//                        assertThat(it.value).isEqualTo(300.0)
//                        assertThat(it.arranged).isFalse
//                    }.anySatisfy {
//                        assertThat(it.payeeId).isEqualTo("3")
//                        assertThat(it.payeeName).isEqualTo("test member 3")
//                        assertThat(it.value).isEqualTo(200.0)
//                        assertThat(it.arranged).isFalse
//                    }
//            }
//            .anySatisfy { member ->
//                assertThat(member.id).isEqualTo("2")
//                assertThat(member.name).isEqualTo("test member 2")
//                assertThat(member.debt).isEqualTo(0.0)
//                assertThat(member.sum).isEqualTo(40.0)
//                assertThat(member.debts).hasSize(1)
//                    .anySatisfy {
//                        assertThat(it.payeeId).isEqualTo("3")
//                        assertThat(it.payeeName).isEqualTo("test member 3")
//                        assertThat(it.value).isEqualTo(100.0)
//                        assertThat(it.arranged).isTrue
//                    }
//            }
//    }
//}