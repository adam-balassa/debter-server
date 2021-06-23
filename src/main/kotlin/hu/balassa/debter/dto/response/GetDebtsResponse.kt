package hu.balassa.debter.dto.response

import hu.balassa.debter.model.Currency

data class GetDebtsResponse(
    val currency: Currency,
    val debts: List<GetDebtMemberResponse>
)

data class GetDebtMemberResponse(
    val id: String,
    val name: String,
    val sum: Double,
    val debt: Double,
    val debts: List<GetDebtResponse>
)


data class GetDebtResponse(
    val payeeId: String,
    val payeeName: String,
    val value: Double,
    val arranged: Boolean
)
