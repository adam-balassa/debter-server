package hu.balassa.debter.unit

import hu.balassa.debter.model.Currency.HUF
import hu.balassa.debter.service.DebtService
import hu.balassa.debter.util.testMember
import hu.balassa.debter.util.testPayment
import hu.balassa.debter.util.testRoom
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DebtServiceTest {
    private val service = DebtService()

    @Test
    fun testArrangementTwoMembers() {
        val room = testRoom(rounding = 10.0, members = listOf(
                testMember(id = "1", payments = listOf(testPayment(convertedValue = 100.0, includedMemberIds = listOf("1", "2"))), debts = emptyList()),
                testMember(id = "2", payments = listOf(), debts = emptyList())))

        service.arrangeDebts(room)

        assertThat(room.members).anySatisfy {
            assertThat(it.id).isEqualTo("1")
            assertThat(it.debts).isEmpty()
        }.anySatisfy { member ->
            assertThat(member.id).isEqualTo("2")
            assertThat(member.debts).hasSize(1).allSatisfy {
                assertThat(it.payeeId).isEqualTo("1")
                assertThat(it.value).isEqualTo(50.0)
                assertThat(it.currency).isEqualTo(HUF)
                assertThat(it.arranged).isFalse
            }
        }
    }

    @Test
    fun testArrangementExcludedMembers() {
        val room = testRoom(rounding = 10.0, members = listOf(
            testMember(id = "1", payments = listOf(testPayment(convertedValue = 300.0, includedMemberIds = listOf("1", "2", "3"))), debts = emptyList()),
            testMember(id = "2", payments = listOf(testPayment(convertedValue = 400.0, includedMemberIds = listOf("1", "2", "3", "4"))), debts = emptyList()),
            testMember(id = "3", payments = listOf(), debts = emptyList()),
            testMember(id = "4", payments = listOf(), debts = emptyList()))
        )

        service.arrangeDebts(room)

        assertThat(room.members).anySatisfy {
            assertThat(it.id).isEqualTo("1")
            assertThat(it.debts).isEmpty()
        }.anySatisfy { member ->
            assertThat(member.id).isEqualTo("2")
            assertThat(member.debts).isEmpty()
        }.anySatisfy { member ->
            assertThat(member.id).isEqualTo("3")
            assertThat(member.debts).hasSize(1).allSatisfy {
                assertThat(it.payeeId).isEqualTo("2")
                assertThat(it.value).isEqualTo(200.0)
                assertThat(it.currency).isEqualTo(HUF)
                assertThat(it.arranged).isFalse
            }
        }.anySatisfy { member ->
            assertThat(member.id).isEqualTo("4")
            assertThat(member.debts).hasSize(1).allSatisfy {
                assertThat(it.payeeId).isEqualTo("1")
                assertThat(it.value).isEqualTo(100.0)
                assertThat(it.currency).isEqualTo(HUF)
                assertThat(it.arranged).isFalse
            }
        }
    }
}