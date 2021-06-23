package hu.balassa.debter.dto.request

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotEmpty

data class AddMemberRequest (
    @field: NotEmpty
    @field: Length(min=3)
    val name: String,
    val includedPaymentIds: List<String>
)
