package hu.balassa.debter.handler

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse

private val objectMapper = objectMapper()
fun <T> sendResponse(status: Int, response: T?) = APIGatewayV2HTTPResponse().apply {
    statusCode = status
    if (response != null)
        body = objectMapper().writeValueAsString(response)
}