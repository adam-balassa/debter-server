package hu.balassa.debter.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import java.time.ZonedDateTime
import kotlin.properties.Delegates

@DynamoDbBean
class Payment {
    lateinit var id: String
    var value by Delegates.notNull<Double>()
    lateinit var currency: Currency
    var convertedValue by Delegates.notNull<Double>()
    lateinit var note: String
    lateinit var date: ZonedDateTime
    var active by Delegates.notNull<Boolean>()
    @Deprecated("Use `split` instead")
    var includedMemberIds: List<String>? = null
    var split: List<Split>? = null
    override fun toString(): String {
        return "Payment(id='$id', value=$value, currency=$currency, convertedValue=$convertedValue, note='$note', date=$date, active=$active, split=$split)"
    }
}

val Payment.safeSplit: List<Split> get() =
    split ?: includedMemberIds?.let {
        it.map { id -> Split(id) }
    } ?: throw IllegalStateException("Both split and included members are null")


@DynamoDbBean
class Split() {
    lateinit var memberId: String
    var units by Delegates.notNull<Int>()
    override fun toString(): String = "Split(memberId=$memberId unit=$units)"

    constructor(memberId: String, units: Int = 1): this() {
        this.memberId = memberId
        this.units = units
    }
}
