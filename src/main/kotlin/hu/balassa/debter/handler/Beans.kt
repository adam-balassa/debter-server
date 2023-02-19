package hu.balassa.debter.handler

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import hu.balassa.debter.client.ExchangeClient
import hu.balassa.debter.config.DynamoDbProductionConfig
import hu.balassa.debter.mapper.ModelDtoMapper
import hu.balassa.debter.repository.RecipeRepositoryImpl
import hu.balassa.debter.service.DebtService
import hu.balassa.debter.service.PaymentService
import hu.balassa.debter.service.RoomService
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator
import org.mapstruct.factory.Mappers
import org.slf4j.LoggerFactory
import java.text.DateFormat.getDateTimeInstance
import javax.validation.Validation

val properties = ApplicationProperties("application.yml")
val dbConfig = DynamoDbProductionConfig()
val debterRepository = RecipeRepositoryImpl(dbConfig.dynamoDB())
val debtService = DebtService(debterRepository)
val mapper = Mappers.getMapper(ModelDtoMapper::class.java)
val exchangeClient = ExchangeClient(properties("fixer.host"), properties("fixer.api-key"))
val paymentService = PaymentService(debterRepository, mapper, exchangeClient, debtService)
val roomService = RoomService(debterRepository, mapper, debtService, exchangeClient)

val log = LoggerFactory.getLogger(Handler::class.java)

fun objectMapper() = ObjectMapper().apply {
    registerModule(JavaTimeModule())
    registerModule(KotlinModule())
    configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
    dateFormat = getDateTimeInstance()
}

fun validator() = Validation
    .byDefaultProvider()
    .configure()
    .messageInterpolator(ParameterMessageInterpolator())
    .buildValidatorFactory()
    .validator