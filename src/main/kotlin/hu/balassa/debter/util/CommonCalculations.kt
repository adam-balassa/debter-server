package hu.balassa.debter.util

import hu.balassa.debter.model.Member
import hu.balassa.debter.model.Payment
import hu.balassa.debter.model.Room

fun memberDebt(member: Member, members: List<Member>): Double =
    if (member.debts.isEmpty()) {
        members.asSequence()
            .flatMap { m -> m.debts }
            .filter { !it.arranged }
            .filter { it.payeeId == member.id }
            .sumOf { - it.value }
    } else {
        member.debts.filter { !it.arranged }.sumOf { it.value }
    }


fun memberSum(member: Member) = member.payments.filter { it.active }.sumOf { it.convertedValue }

fun memberIdToName(memberId: String, members: List<Member>) = members.find { it.id == memberId }?.name ?: ""

fun paymentsWithMembers(members: List<Member>): List<Pair<String, Payment>> =
    members.flatMap { it.payments.map { payment -> it.id to payment } }