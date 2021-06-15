package hu.balassa.debter.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import kotlin.properties.Delegates

@DynamoDbBean
class DebtArrangement {
    lateinit var payeeId: String
    var value by Delegates.notNull<Double>()
    lateinit var currency: Currency
    var arranged by Delegates.notNull<Boolean>()
    override fun toString(): String {
        return "DebtArrangement(payeeId='$payeeId', value=$value, currency=$currency, arranged=$arranged)"
    }
}