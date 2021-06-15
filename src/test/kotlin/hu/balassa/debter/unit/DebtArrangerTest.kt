package hu.balassa.debter.unit

import hu.balassa.debter.service.DebtArranger
import hu.balassa.debter.service.SimpleMember
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DebtArrangerTest {
    @Test
    fun arrangeDebtsTwoMembers() {
        val claims = mutableListOf(SimpleMember("1", -200.0))
        val debts = mutableListOf(SimpleMember("2", 200.0))
        val rounding = 10.0

        val result = DebtArranger(claims, debts, rounding).arrange()

        assertThat(result).hasSize(1).allSatisfy {
            assertThat(it.fromId).isEqualTo("2")
            assertThat(it.toId).isEqualTo("1")
            assertThat(it.amount).isEqualTo(200.0)
        }
    }

    @Test
    fun arrangeDebtsTwoMembersRounding() {
        val claims = mutableListOf(SimpleMember("1", -209.0))
        val debts = mutableListOf(SimpleMember("2", 209.0))
        val rounding = 10.0

        val result = DebtArranger(claims, debts, rounding).arrange()

        assertThat(result).hasSize(1).allSatisfy {
            assertThat(it.fromId).isEqualTo("2")
            assertThat(it.toId).isEqualTo("1")
            assertThat(it.amount).isEqualTo(210.0)
        }
    }

    @Test
    fun arrangeDebtsTwoClaims() {
        val claims = mutableListOf(SimpleMember("1", -200.0), SimpleMember("3", -300.0))
        val debts = mutableListOf(SimpleMember("2", 500.0))
        val rounding = 10.0

        val result = DebtArranger(claims, debts, rounding).arrange()

        assertThat(result).hasSize(2).anySatisfy {
            assertThat(it.fromId).isEqualTo("2")
            assertThat(it.toId).isEqualTo("1")
            assertThat(it.amount).isEqualTo(200.0)
        }.anySatisfy {
            assertThat(it.fromId).isEqualTo("2")
            assertThat(it.toId).isEqualTo("3")
            assertThat(it.amount).isEqualTo(300.0)
        }
    }

    @Test
    fun arrangeDebtsTwoDebts() {
        val claims = mutableListOf(SimpleMember("2", -500.0))
        val debts = mutableListOf(SimpleMember("1", 200.0), SimpleMember("3", 300.0))
        val rounding = 10.0

        val result = DebtArranger(claims, debts, rounding).arrange()

        assertThat(result).hasSize(2).anySatisfy {
            assertThat(it.fromId).isEqualTo("1")
            assertThat(it.toId).isEqualTo("2")
            assertThat(it.amount).isEqualTo(200.0)
        }.anySatisfy {
            assertThat(it.fromId).isEqualTo("3")
            assertThat(it.toId).isEqualTo("2")
            assertThat(it.amount).isEqualTo(300.0)
        }
    }


    @Test
    fun arrangeDebtsFiveMembers() {
        val claims = mutableListOf(SimpleMember("3", -150.0), SimpleMember("4", -100.0), SimpleMember("5", -200.0))
        val debts = mutableListOf(SimpleMember("1", 175.0), SimpleMember("2", 275.0))
        val rounding = 10.0

        val result = DebtArranger(claims, debts, rounding).arrange()

        assertThat(result).hasSize(4).anySatisfy {
            assertThat(it.fromId).isEqualTo("1")
            assertThat(it.toId).isEqualTo("5")
            assertThat(it.amount).isEqualTo(180.0)
        }.anySatisfy {
            assertThat(it.fromId).isEqualTo("2")
            assertThat(it.toId).isEqualTo("3")
            assertThat(it.amount).isEqualTo(150.0)
        }.anySatisfy {
            assertThat(it.fromId).isEqualTo("2")
            assertThat(it.toId).isEqualTo("4")
            assertThat(it.amount).isEqualTo(100.0)
        }.anySatisfy {
            assertThat(it.fromId).isEqualTo("2")
            assertThat(it.toId).isEqualTo("5")
            assertThat(it.amount).isEqualTo(20.0)
        }
    }
}