package hu.balassa.debter.util

import hu.balassa.debter.handler.objectMapper
import hu.balassa.debter.model.*
import java.io.File
import java.io.InputStream
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.text.Charsets.UTF_8

fun testRoom(
    key: String = "TESTKEY",
    name: String = "Test room",
    currency: Currency = Currency.HUF,
    rounding: Double = 10.0,
    members: List<Member> = listOf(testMember("member1", "test member 1"), testMember("member2", "test member 2"))
) = Room().also {
    it.key = key
    it.name = name
    it.currency = currency
    it.rounding = rounding
    it.members = members
}

fun testMember(
    id: String = "member0",
    name: String = "test member $id",
    payments: List<Payment> = listOf(testPayment("${id}payment1"), testPayment("${id}payment2")),
    debts: List<DebtArrangement> = listOf(testDebt(payeeId = if (id == "member1") "member2" else "member1"))
) = Member().also {
    it.id = id
    it.name = name
    it.payments = payments
    it.debts = debts
}

fun testPayment(
    id: String = "member0payment0",
    active: Boolean = true,
    convertedValue: Double = 20.0,
    currency: Currency = Currency.HUF,
    date: ZonedDateTime = dateOf(2020, 9, 1),
    includedMemberIds: List<String> = listOf("member1", "member2"),
    note: String = "test note",
    value: Double = 20.0,
    split: List<Split>? = null
) = Payment().also {
    it.id = id
    it.active = active
    it.convertedValue = convertedValue
    it.currency = currency
    it.date = date
    it.split = split ?: includedMemberIds.map { id -> Split(id)}
    it.note = note
    it.value = value
}

fun testDebt(
    arranged: Boolean = false,
    currency: Currency = Currency.HUF,
    value: Double = 20.0,
    payeeId: String = "member0"
) = DebtArrangement().also {
    it.currency = currency
    it.arranged = arranged
    it.payeeId = payeeId
    it.value = value
}

fun dateOf(
    year: Int, month: Int, dayOfMonth: Int,
    hour: Int = 12, minute: Int = 30, second: Int = 0
) = ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, 0, ZoneId.of("CET"))


inline fun <reified T> loadJsonFile(fileName: String) =
    objectMapper().readValue(readFile(fileName), T::class.java)

//fun loadJsonFile(fileName: String): String = readFile(fileName)!!.readAllBytes().joinToString("")


fun readFile(fileName: String): InputStream? =
    Thread.currentThread().contextClassLoader.getResourceAsStream("TEST_MOCK/$fileName")
