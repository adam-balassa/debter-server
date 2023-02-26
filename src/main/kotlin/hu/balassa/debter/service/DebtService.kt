package hu.balassa.debter.service

import hu.balassa.debter.dto.request.AddPaymentRequest
import hu.balassa.debter.dto.response.GetDebtMemberResponse
import hu.balassa.debter.dto.response.GetDebtResponse
import hu.balassa.debter.dto.response.GetDebtsResponse
import hu.balassa.debter.model.*
import hu.balassa.debter.repository.DebterRepository
import hu.balassa.debter.util.*
import kotlin.math.absoluteValue

open class DebtService(private val repository: DebterRepository) {

    fun getDebts(roomKey: String): GetDebtsResponse = repository.loadRoom(roomKey) { room ->
        GetDebtsResponse(
            room.currency,
            room.members.filter { it.debts.isNotEmpty() }
                .map { member ->
                    GetDebtMemberResponse(
                        member.id,
                        member.name,
                        memberSum(member),
                        memberDebt(member, room.members),
                        member.debts.map {
                            GetDebtResponse(
                                it.payeeId,
                                memberIdToName(it.payeeId, room.members),
                                it.value,
                                it.arranged
                            )
                        })
                }
        )
    }


    fun arrangeDebtForPayment(newPayment: AddPaymentRequest, room: Room) {
        if (checkForExistingDebtArrangement(newPayment, room))
            return
        arrangeDebts(room)
    }

    private fun checkForExistingDebtArrangement(newPayment: AddPaymentRequest, room: Room): Boolean {
        val member = room.members.find { it.id == newPayment.memberId }!!
        val includedMemberId = if (newPayment.split.size == 1) newPayment.split[0].memberId else return false
        val suitableDebt = member.debts.find {
            it.value.isAround(newPayment.value, room.rounding) &&
                    !it.arranged &&
                    it.currency == newPayment.currency &&
                    it.payeeId == includedMemberId
        } ?: return false
        suitableDebt.arranged = true
        return true
    }

    fun arrangeDebts(room: Room) {
        val memberDebts = getMemberDebts(room.members)

        val (claims, debts) = memberDebts.partition { it.debt < 0.0 }
        val debtArranger = DebtArranger(claims, debts, room.rounding)
        val arrangements = debtArranger.arrange()

        setMemberDebts(room, arrangements)
    }

    private fun setMemberDebts(room: Room, arrangements: List<SimpleDebtArrangement>) {
        val debts = arrangements.groupBy { it.fromId }
        room.members.forEach { it.debts = emptyList() }
        room.members.forEach { member ->
            debts[member.id]?.let { memberDebts ->
                member.debts = memberDebts.map {
                    DebtArrangement().apply {
                        payeeId = it.toId
                        value = it.amount
                        currency = room.currency
                        arranged = false
                    }
                }
            }
        }
    }

    private fun getMemberDebts(members: List<Member>): List<SimpleMember> {
        val memberDebts = mutableMapOf<String, Double>().apply {
            putAll(members.map { it.id to 0.0 })
        }
        paymentsWithMembers(members)
            .filter { (_, payment) -> payment.active }
            .forEach { (memberId, payment) ->
                shareForMembers(payment.safeSplit, payment.convertedValue).forEach { (memberWithDebt, debtValue) ->
                    memberDebts[memberWithDebt] = memberDebts[memberWithDebt]!! + debtValue
                }
                memberDebts[memberId] = memberDebts[memberId]!! - payment.convertedValue
            }

        return memberDebts.map { (memberId, debt) -> SimpleMember(memberId, debt) }
    }
}

data class SimpleMember(val memberId: String, var debt: Double)
data class SimpleDebtArrangement(val fromId: String, val toId: String, val amount: Double)

fun Double.isAround(other: Double, rounding: Double) = (this - other).absoluteValue < rounding / 2