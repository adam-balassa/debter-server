package hu.balassa.debter.dto.response

import hu.balassa.debter.model.Currency
import java.time.ZonedDateTime

data class GetPaymentsResponse (
    val activePayments: List<GetPaymentResponse>,
    val deletedPayments: List<GetPaymentResponse>,
)

data class GetPaymentResponse (
    val id: String,
    val memberName: String,
    val value: Double,
    val convertedValue: Double,
    val currency: Currency,
    val note: String,
    val date: ZonedDateTime,
    val includedMembers: List<MemberIncluded>
)

data class MemberIncluded(val memberName: String, val included: Boolean)
