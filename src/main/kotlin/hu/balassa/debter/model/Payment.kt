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
    lateinit var includedMemberIds: List<String>
    override fun toString(): String {
        return "Payment(id='$id', value=$value, currency=$currency, convertedValue=$convertedValue, note='$note', date=$date, active=$active, includedMemberIds=$includedMemberIds)"
    }
}
