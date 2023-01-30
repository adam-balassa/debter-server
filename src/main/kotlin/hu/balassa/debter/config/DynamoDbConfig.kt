package hu.balassa.debter.config

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region.EU_CENTRAL_1
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement
import software.amazon.awssdk.services.dynamodb.model.KeyType.HASH
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType.S
import java.net.URI

abstract class DynamoDbConfig {
    companion object {
        const val tableName = "debter"
    }

    abstract fun dynamoDbClient(): DynamoDbClient

    open fun dynamoDB(): DynamoDbEnhancedClient {
        val db = dynamoDbClient()

        if (!db.listTables().tableNames().contains(tableName)) {
            createTable(db)
        }

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

open class DynamoDbStaticConfig: DynamoDbConfig() {
    private lateinit var dbEndpoint: String
    private lateinit var tableName: String
    private lateinit var awsAccessKey: String
    private lateinit var awsSecretKey: String

    override fun dynamoDB(): DynamoDbEnhancedClient {
        return super.dynamoDB()
    }

    override fun dynamoDbClient(): DynamoDbClient = DynamoDbClient
        .builder()
        .region(EU_CENTRAL_1)
        .credentialsProvider { awsCredentials() }
        .endpointOverride(URI.create(dbEndpoint))
        .build()

    private fun awsCredentials(): AwsCredentials {
        return AwsBasicCredentials.create(awsAccessKey, awsSecretKey)
    }
}

public open class DynamoDbProductionConfig: DynamoDbConfig() {
    override fun dynamoDB(): DynamoDbEnhancedClient {
        return super.dynamoDB()
    }
    
    override fun dynamoDbClient(): DynamoDbClient = DynamoDbClient
        .builder()
        .region(EU_CENTRAL_1)
        .build()

}
