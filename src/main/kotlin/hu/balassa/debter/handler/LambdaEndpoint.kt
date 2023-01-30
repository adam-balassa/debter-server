package hu.balassa.debter.handler

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hu.balassa.debter.exception.withErrorHandling
import javax.validation.Validation

open class LambdaEndpoint<RequestBody, Response>(
    private val status: Int,
    private val bodyType: Class<RequestBody>? = null,
    private val endpoint: (Request<RequestBody>) -> Response?
) : RequestHandler<Map<String, Any>, ApiGatewayResponse> {
    private val validator = Validation.buildDefaultValidatorFactory().validator
    private val objectMapper = jacksonObjectMapper()

    override fun handleRequest(event: Map<String, Any>, context: Context): ApiGatewayResponse = withErrorHandling {
        val queryParams = event["query"] as Map<String, String>
        val pathParams = event["path"] as Map<String, String>
        val body = bodyType?.let{
            val bodyString = event["body"] as String
            val bodyObject = objectMapper.readValue(bodyString, bodyType)
            validator.validate(bodyObject, bodyType)
            bodyObject
        }
        val response = endpoint(Request(queryParams, pathParams, body))
        ApiGatewayResponse.build {
            statusCode = status
            response?.let { objectBody = it }
        }
    }
}

data class Request <T> (
    val queryParams: Map<String, String>,
    val pathParams: Map<String, String>,
    val body: T?,
)