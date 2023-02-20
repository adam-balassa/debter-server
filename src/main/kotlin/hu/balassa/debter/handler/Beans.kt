package hu.balassa.debter.handler

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hu.balassa.debter.client.ExchangeClient
import hu.balassa.debter.config.DynamoDbProductionConfig
import hu.balassa.debter.mapper.ModelDtoMapper
import hu.balassa.debter.repository.DebterRepository
import hu.balassa.debter.repository.RecipeRepositoryImpl
import hu.balassa.debter.service.DebtService
import hu.balassa.debter.service.PaymentService
import hu.balassa.debter.service.RoomService
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator
import org.mapstruct.factory.Mappers
import org.slf4j.LoggerFactory
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import java.text.DateFormat.getDateTimeInstance
import javax.validation.Validation

annotation class Mockable

@Mockable
class Application {
    val properties = ApplicationProperties("application.yml")
    fun dbConfig() = DynamoDbProductionConfig()
    fun debterRepository(db: DynamoDbEnhancedClient?): DebterRepository = RecipeRepositoryImpl(db)
    fun debtService(repository: DebterRepository) = DebtService(repository)
    fun mapper() = Mappers.getMapper(ModelDtoMapper::class.java)
    fun exchangeClient() = ExchangeClient(properties("fixer.host"), properties("fixer.api-key"))
    fun paymentService(repository: DebterRepository, mapper: ModelDtoMapper, client: ExchangeClient, service: DebtService) =
        PaymentService(repository, mapper, client, service)
    fun roomService(repository: DebterRepository, mapper: ModelDtoMapper, client: ExchangeClient, service: DebtService)
            = RoomService(repository, mapper, service, client)

    lateinit var repository: DebterRepository
    lateinit var debtService: DebtService
    lateinit var exchangeClient: ExchangeClient
    lateinit var mapper: ModelDtoMapper
    lateinit var paymentService: PaymentService
    lateinit var roomService: RoomService

    fun init() {
        repository = debterRepository(dbConfig().dynamoDB())
        debtService = debtService(repository)
        exchangeClient = exchangeClient()
        mapper = mapper()
        paymentService = paymentService(repository, mapper, exchangeClient, debtService)
        roomService = roomService(repository, mapper, exchangeClient, debtService)
    }
}

val log = LoggerFactory.getLogger(Handler::class.java)

fun objectMapper() = jacksonObjectMapper().apply {
    registerModule(JavaTimeModule())
    configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
    dateFormat = getDateTimeInstance()
}

fun validator() = Validation
    .byDefaultProvider()
    .configure()
    .messageInterpolator(ParameterMessageInterpolator())
    .buildValidatorFactory()
    .validator!!