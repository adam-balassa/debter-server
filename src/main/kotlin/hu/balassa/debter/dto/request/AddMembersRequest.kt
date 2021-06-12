package hu.balassa.debter.dto.request

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

data class AddMembersRequest (
    @field: Size(min = 3)
    @field: NotEmpty
    val memberNames: Set<String>
)
