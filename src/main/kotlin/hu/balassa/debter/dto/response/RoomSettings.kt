package hu.balassa.debter.dto.response

import hu.balassa.debter.model.Currency

data class RoomSettings (
    val currency: Currency,
    val rounding: Double
)
