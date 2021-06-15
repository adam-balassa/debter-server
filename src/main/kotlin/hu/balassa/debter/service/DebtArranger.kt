package hu.balassa.debter.service

import kotlin.math.absoluteValue
import kotlin.math.floor

class DebtArranger (
    membersWithClaims: List<SimpleMember>,
    membersWithDebts: List<SimpleMember>,
    private val rounding: Double
) {
    private val debtArrangements = mutableListOf<SimpleDebtArrangement>()
    private val membersWithClaims = membersWithClaims.map { SimpleMember(it.memberId, it.debt.absoluteValue) }.toMutableList()
    private val membersWithDebts = membersWithDebts.toMutableList()

    fun arrange(): List<SimpleDebtArrangement> {
        fun isDebtArrangementDone() = membersWithClaims.isEmpty() || membersWithDebts.isEmpty()

        sort()
        var i = 0
        while (!isDebtArrangementDone()) {
            i += 1
            if (tryPerfectArrangement())
                continue
            if (isDebtArrangementDone() || i == 20)
                return debtArrangements
            if (tryDebtArrangement())
                continue
            arrangeDebt(membersWithDebts.first(), membersWithClaims.first(), membersWithClaims.first().debt)
        }
        return debtArrangements
    }

    private fun tryPerfectArrangement(): Boolean {
        membersWithDebts.forEach { memberWithDebts ->
            membersWithClaims.forEach { memberWithClaims ->
                if (memberWithClaims.debt.isAround(memberWithDebts.debt, rounding)) {
                    arrangeDebt(memberWithDebts, memberWithClaims, memberWithClaims.debt)
                    return true
                }
            }
        }
        return false
    }

    private fun tryDebtArrangement(): Boolean {
        val memberWithMostClaims = membersWithClaims.first()
        val memberWithMostSuitableDebt = membersWithDebts.firstOrNull { it.debt < memberWithMostClaims.debt }
            ?: return false
        arrangeDebt(memberWithMostSuitableDebt, memberWithMostClaims, memberWithMostSuitableDebt.debt)
        return true
    }


    private fun arrangeDebt(memberWithDebts: SimpleMember, memberWithClaims: SimpleMember, originalAmount: Double) {
        val arrangedAmount = floor((originalAmount + rounding / 2) / rounding) * rounding
        debtArrangements.add(SimpleDebtArrangement(memberWithDebts.memberId, memberWithClaims.memberId, arrangedAmount))
        memberWithClaims.debt -= arrangedAmount
        memberWithDebts.debt -= arrangedAmount

        if (memberWithClaims.debt <= rounding / 2.0)
            membersWithClaims.remove(memberWithClaims)
        if (memberWithDebts.debt <= rounding / 2.0)
            membersWithDebts.remove(memberWithDebts)

        sort()
    }

    private fun sort() {
        membersWithClaims.sortByDescending { it.debt }
        membersWithDebts.sortByDescending { it.debt }
    }
}