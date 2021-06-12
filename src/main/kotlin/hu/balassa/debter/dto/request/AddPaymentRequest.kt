package hu.balassa.debter.dto.request

import hu.balassa.debter.model.Currency
import org.hibernate.validator.constraints.Length
import java.time.ZonedDateTime
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Past

data class AddPaymentRequest (
    @field: NotNull
    val value: Double,

    @field: NotEmpty
    val memberId: String,

    @NotNull
    @field: Past
    val date: ZonedDateTime,

    @field: NotNull
    val currency: Currency,

    @field: NotEmpty
    @field: Length(min = 3)
    val note: String,

    @field: NotEmpty
    val included: List<String>
)