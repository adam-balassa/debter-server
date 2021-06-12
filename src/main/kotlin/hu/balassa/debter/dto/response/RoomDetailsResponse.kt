package hu.balassa.debter.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import hu.balassa.debter.model.Currency
import java.time.ZonedDateTime

data class RoomDetailsResponse (
    val payments: List<PaymentResponse>,
    val members: List<MemberResponse>,
    val debts: List<DebtResponse>,
    val roomKey: String,
    val name: String,
    val defaultCurrency: Currency,
    val rounding: Double
)

data class MemberResponse (
    val id: String,
    val name: String
)

data class DebtResponse (
    val value: Double,
    val currency: Currency,
    @JsonProperty("for")
    val to: String,
    val from: String,
    val arranged: Boolean
)

data class PaymentResponse (
    val id: String,
    val memberId: String,
    val value: Double,
    val currency: Currency,
    val realValue: Double,
    val note: String,
    val date: ZonedDateTime,
    val active: Boolean,
    val included: List<String>
)