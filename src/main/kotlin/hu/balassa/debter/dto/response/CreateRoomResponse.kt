package hu.balassa.debter.dto.response

import hu.balassa.debter.model.Currency

data class CreateRoomResponse (
    val roomKey: String,
    val name: String,
    val currency: Currency,
    val rounding: Double
)