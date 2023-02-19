package hu.balassa.debter.exception

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import hu.balassa.debter.handler.log
import hu.balassa.debter.handler.sendResponse
import org.apache.commons.lang3.exception.ExceptionUtils
import javax.validation.ValidationException


fun withErrorHandling(body: () -> APIGatewayV2HTTPResponse): APIGatewayV2HTTPResponse =
    try { body() }
    catch (e: IllegalArgumentException) { badRequest(e) }
    catch (e: ValidationException) { invalidRequest(e) }
    catch (e: Exception) { internalServerError(e) }


private fun badRequest(exception: IllegalArgumentException)= sendResponse(404,
    ErrorResponse("Bad request", exception.message ?: "Illegal arguments provided")
).also { log.error("Bad request\n${ ExceptionUtils.getStackTrace(exception) }") }

private fun invalidRequest(exception: ValidationException) = sendResponse(404,
    ErrorResponse("Bad request", exception.localizedMessage ?: "Invalid request body")
).also { log.error("Invalid request\n${ ExceptionUtils.getStackTrace(exception) }") }

private fun internalServerError(exception: Exception) = sendResponse(500,
    ErrorResponse("Internal server error", exception.message ?: "Something went wrong")
).also { log.error("Internal error\n${ ExceptionUtils.getStackTrace(exception) }") }

data class ErrorResponse(
    val error: String,
    val reason: String
)
