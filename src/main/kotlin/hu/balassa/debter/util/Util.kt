package hu.balassa.debter.util

import hu.balassa.debter.model.Room
import hu.balassa.debter.repository.DebterRepository
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

fun <T> DebterRepository.withRoom(roomKey: String, body: (_: Room) -> T): T = findByKey(roomKey)?.let {
        val result = body(it)
        save(it)
        return result
    } ?: throw IllegalArgumentException("Invalid room key: $roomKey")