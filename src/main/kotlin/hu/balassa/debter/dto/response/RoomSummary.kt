package hu.balassa.debter.dto.response

import hu.balassa.debter.model.Currency

data class RoomSummary (
    val roomKey: String,
    val name: String,
    val sum: Double,
    val currency: Currency,
    val memberSummary: List<MemberSummary>
)

data class MemberSummary (
    val name: String,
    val sum: Double,
    val debt: Double
)