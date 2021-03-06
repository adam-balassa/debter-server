package hu.balassa.debter.dto.request

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

data class AddMembersRequest (
    @field: Size(min = 3)
    @field: NotEmpty
    val members: Set<@NotEmpty @Length(min=3) String>
)
