package hu.balassa.debter.service

import hu.balassa.debter.model.Member
import hu.balassa.debter.model.Room
import org.springframework.stereotype.Service

@Service
open class DebtArrangementService {
    open fun arrangeDebts(room: Room) {

    }
}

data class MemberWrapper (
    val member: Member,
    val sum: Double,
    val debt: Double,
    var debts: MutableList<InternalPayment>
) {
    override fun equals(other: Any?): Boolean {
        return if (other is MemberWrapper) {
            other.member.id == member.id
        } else {
            super.equals(other)
        }
    }

    override fun hashCode(): Int {
        return member.id.hashCode()
    }
}

data class InternalPayment (
    val value: Double,
    val from: MemberWrapper,
    val to: MemberWrapper
)