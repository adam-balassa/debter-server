package hu.balassa.debter.util

import hu.balassa.debter.model.Room
import hu.balassa.debter.repository.DebterRepository
import java.time.ZonedDateTime
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

fun <T> DebterRepository.useRoom(roomKey: String, body: (_: Room) -> T): T = loadRoom(roomKey) {
    val result = body(it)
    it.apply { lastModified = ZonedDateTime.now() }
    save(it)
    result
}


fun <T> DebterRepository.loadRoom(roomKey: String, body: (_: Room) -> T): T = findByKey(roomKey)?.let {
    body(it)
} ?: throw IllegalArgumentException("Invalid room key: $roomKey")