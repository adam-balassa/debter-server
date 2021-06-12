package hu.balassa.debter.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region.EU_CENTRAL_1
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement
import software.amazon.awssdk.services.dynamodb.model.KeyType.HASH
import software.amazon.awssdk.services.dynamodb.model.KeyType.RANGE
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
                    .attributeName("id").keyType(HASH)
                    .attributeName("key").keyType(RANGE)
                    .build()
                )
                .attributeDefinitions(
                    AttributeDefinition.builder().attributeName("id").attributeType(S).build())
                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(1L).writeCapacityUnits(1L).build())
                .build()
        )
    }
}

@Configuration
@Profile("develop")
open class DynamoDbStaticConfig: DynamoDbConfig() {
    @Value("\${amazon.dynamodb.endpoint}")
    private lateinit var dbEndpoint: String

    @Value("\${amazon.dynamodb.tableName}")
    private lateinit var tableName: String

    @Value("\${amazon.aws.accessKey}")
    private lateinit var awsAccessKey: String

    @Value("\${amazon.aws.secretKey}")
    private lateinit var awsSecretKey: String

    @Bean
    override fun dynamoDB(): DynamoDbEnhancedClient {
        return super.dynamoDB()
    }

    @Bean
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

@Configuration
@Profile("production")
open class DynamoDbProductionConfig: DynamoDbConfig() {
    @Value("\${amazon.aws.accessKey}")
    private lateinit var awsAccessKey: String

    @Value("\${amazon.aws.secretKey}")
    private lateinit var awsSecretKey: String

    @Bean
    override fun dynamoDB(): DynamoDbEnhancedClient {
        return super.dynamoDB()
    }

    @Bean
    override fun dynamoDbClient(): DynamoDbClient = DynamoDbClient
        .builder()
        .region(EU_CENTRAL_1)
        .credentialsProvider { awsCredentials() }
        .build()

    private fun awsCredentials(): AwsCredentials {
        return AwsBasicCredentials.create(awsAccessKey, awsSecretKey)
    }
}
