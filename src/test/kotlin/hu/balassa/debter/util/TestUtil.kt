package hu.balassa.debter.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hu.balassa.debter.model.Currency
import hu.balassa.debter.model.DebtArrangement
import hu.balassa.debter.model.Member
import hu.balassa.debter.model.Payment
import hu.balassa.debter.model.Room
import org.springframework.core.io.ClassPathResource
import org.springframework.test.web.reactive.server.WebTestClient
import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.time.ZoneId
import java.time.ZonedDateTime

fun testRoom(
    key: String,
    name: String = "Test room",
    currency: Currency = Currency.HUF,
    rounding: Double = 10.0,
    members: List<Member> = listOf(testMember("test member 1", "member1"), testMember("test member 2", "member2"))
) = Room().also {
    it.key = key
    it.name = name
    it.currency = currency
    it.rounding = rounding
    it.members = members
}

fun testMember(
    name: String = "test member 0",
    id: String = "member0",
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
    includedMemberIds: List<String> = listOf("1", "2"),
    note: String = "test note",
    value: Double = 20.0
) = Payment().also {
    it.id = id
    it.active = active
    it.convertedValue = convertedValue
    it.currency = currency
    it.date = date
    it.includedMemberIds = includedMemberIds
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


inline fun <reified T> WebTestClient.ResponseSpec.responseBody() =
    expectBody(T::class.java).returnResult().responseBody!!

inline fun <reified T> loadJsonFile(fileName: String) =
    jacksonObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
        .readValue(getFile(fileName), T::class.java)

fun loadJsonFile(fileName: String): String = getFile(fileName)!!.readLines(UTF_8).joinToString("")


fun getFile(fileName: String): File? = ClassPathResource("TEST_MOCK/$fileName").file
