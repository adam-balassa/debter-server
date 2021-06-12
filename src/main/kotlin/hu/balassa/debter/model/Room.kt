package hu.balassa.debter.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.time.ZonedDateTime
import kotlin.properties.Delegates


@DynamoDbBean
class Room {
    @get:DynamoDbPartitionKey
    var id: String? = null

    @get: DynamoDbSortKey
    lateinit var key: String

    lateinit var name: String

    lateinit var members: List<Member>

    lateinit var currency: Currency

    var rounding by Delegates.notNull<Double>()

    lateinit var lastModified: ZonedDateTime
}