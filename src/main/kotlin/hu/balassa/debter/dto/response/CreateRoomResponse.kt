package hu.balassa.debter.dto.response

import hu.balassa.debter.model.Currency

data class CreateRoomResponse (
    val key: String,
    val defaultCurrency: Currency,
    val rounding: Double
)