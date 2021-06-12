package hu.balassa.debter.model

import hu.balassa.debter.model.Currency.HUF
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey


@DynamoDbBean
class Room {
    @get:DynamoDbPartitionKey
    var id: String? = null

    @get: DynamoDbSortKey
    var key: String = ""

    var name: String = ""

    var currency: Currency = HUF

    var rounding: Double = 10.0
}