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
    val split: List<MemberShare>
)

data class MemberShare(val memberName: String, val share: Double)
