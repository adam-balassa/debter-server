package hu.balassa.debter.util

import java.util.UUID

fun generateRoomKey(bannedValues: List<String>): String {
    var roomKey: String
    do {
        roomKey = generateRoomKey()
    } while (roomKey in bannedValues)
    return roomKey
}

private fun generateRoomKey() = (1..6)
    .map { ('A'..'Z').random() }
    .joinToString("")


fun generateUUID(): String = UUID.randomUUID().toString()