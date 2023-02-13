package hu.balassa.debter.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import java.time.ZonedDateTime
import kotlin.properties.Delegates


@DynamoDbBean
class Room {
    @get: DynamoDbPartitionKey
    var key: String? = null

    lateinit var name: String

    lateinit var members: List<Member>

    lateinit var currency: Currency

    var rounding by Delegates.notNull<Double>()

    lateinit var lastModified: ZonedDateTime
}