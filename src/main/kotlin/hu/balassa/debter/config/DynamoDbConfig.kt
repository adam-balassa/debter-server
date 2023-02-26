package hu.balassa.debter.config

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region.EU_CENTRAL_1
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement
import software.amazon.awssdk.services.dynamodb.model.KeyType.HASH
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType.S

abstract class DynamoDbConfig {
    companion object {
        const val tableName = "debter"
    }

    abstract fun dynamoDbClient(): DynamoDbClient

    open fun dynamoDB(): DynamoDbEnhancedClient {
        val db = dynamoDbClient()
        try {
            if (!db.listTables().tableNames().contains(tableName)) {
                createTable(db)
            }
        } catch(e: Exception) {}

        return DynamoDbEnhancedClient.builder().dynamoDbClient(db).build()
    }

    private fun createTable(db: DynamoDbClient) {
        db.createTable(
            CreateTableRequest
                .builder()
                .tableName(tableName)
                .keySchema(KeySchemaElement.builder()
                    .attributeName("key").keyType(HASH)
                    .build()
                )
                .attributeDefinitions(
                    AttributeDefinition.builder().attributeName("key").attributeType(S).build())
                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(1L).writeCapacityUnits(1L).build())
                .build()
        )
    }
}

open class DynamoDbProductionConfig: DynamoDbConfig() {
    override fun dynamoDbClient(): DynamoDbClient = DynamoDbClient
        .builder()
        .region(EU_CENTRAL_1)
        .build()
}
