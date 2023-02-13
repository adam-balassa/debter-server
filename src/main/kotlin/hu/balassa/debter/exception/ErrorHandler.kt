package hu.balassa.debter.exception

import hu.balassa.debter.handler.ApiGatewayResponse
import javax.validation.ValidationException


fun withErrorHandling(body: () -> ApiGatewayResponse): ApiGatewayResponse =
    try { body() }
    catch (e: IllegalArgumentException) { badRequest(e) }
    catch (e: ValidationException) { invalidRequest(e) }
    catch (e: Exception) { internalServerError(e) }


private fun badRequest(exception: IllegalArgumentException) = ApiGatewayResponse.build {
    statusCode = 404
    objectBody = ErrorResponse("Bad request", exception.message ?: "Illegal arguments provided")
}

private fun invalidRequest(exception: ValidationException) = ApiGatewayResponse.build {
    statusCode = 404
    objectBody = ErrorResponse("Bad request", exception.localizedMessage ?: "Invalid request body")
}

private fun internalServerError(exception: Exception) = ApiGatewayResponse.build {
    statusCode = 500
    objectBody = ErrorResponse("Internal server error", exception.message ?: "Something went wrong")
}.also { exception.printStackTrace() }

data class ErrorResponse(
    val error: String,
    val reason: String
)
