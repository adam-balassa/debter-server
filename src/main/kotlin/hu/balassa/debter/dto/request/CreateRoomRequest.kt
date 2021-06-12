package hu.balassa.debter.dto.request

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotEmpty


data class CreateRoomRequest (
    @field: Length(min = 3)
    @field: NotEmpty
    val name: String
)