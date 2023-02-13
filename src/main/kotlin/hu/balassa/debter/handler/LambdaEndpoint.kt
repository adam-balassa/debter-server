package hu.balassa.debter.handler

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import hu.balassa.debter.exception.withErrorHandling
import java.lang.IllegalArgumentException

open class LambdaEndpoint<RequestBody, Response>(
    private val status: Int,
    private val bodyType: Class<RequestBody>? = null,
    private val endpoint: Request<RequestBody>.() -> Response?
) : RequestHandler<Map<String, Any>, ApiGatewayResponse> {
    private val validator = validator()
    private val objectMapper = objectMapper()

    override fun handleRequest(event: Map<String, Any>, context: Context?): ApiGatewayResponse = withErrorHandling {
        val queryParams = event["queryStringParameters"] as? Map<String, String> ?: emptyMap()
        val pathParams = event["pathParameters"] as? Map<String, String> ?: emptyMap()
        val body = bodyType?.let{
            val bodyString = event["body"] as String
            val bodyObject = objectMapper.readValue(bodyString, bodyType)
            validator.validate(bodyObject)
            bodyObject
        }
        val response = endpoint(Request(queryParams, pathParams, body))
        ApiGatewayResponse.build {
            statusCode = status
            if (response != Unit) response?.let { objectBody = it }
        }
    }
}

class Request <T> (
    private val queryParams: Map<String, String>,
    private val pathParams: Map<String, String>,
    private val _body: T?,
) {
    val body get() = _body ?: throw IllegalArgumentException("Request body is missing")
    fun pathVariable (key: String) = pathParams[key] ?: throw IllegalArgumentException("$key is missing from path")
    fun queryVariable (key: String) = queryParams[key] ?: throw IllegalArgumentException("$key is missing from query")
}