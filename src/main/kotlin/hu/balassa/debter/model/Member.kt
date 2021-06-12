package hu.balassa.debter.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean

@DynamoDbBean
class Member {
    lateinit var id: String
    lateinit var name: String
    lateinit var payments: List<Payment>
    lateinit var debts: List<DebtArrangement>
}