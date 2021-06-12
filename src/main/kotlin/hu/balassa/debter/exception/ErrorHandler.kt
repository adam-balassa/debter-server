package hu.balassa.debter.exception

import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.Locale


@ControllerAdvice
class ErrorHandler(
    private val messageSource: MessageSource
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(IllegalArgumentException::class, HttpMessageNotReadableException::class)
    fun badRequest(exception: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse("Bad request", exception.message ?: "Illegal arguments provided"),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun invalidRequest(exception: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse("Bad request",
                exception.bindingResult.fieldError?.let {
                    "${it.field} ${messageSource.getMessage(it, Locale.ENGLISH)}"
                } ?: "Invalid request body"),
            HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun internalServerError(exception: java.lang.Exception): ResponseEntity<ErrorResponse> {
        log.error(exception.message, exception)
        return ResponseEntity(
            ErrorResponse("Internal server error", exception.localizedMessage),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}

data class ErrorResponse(
    val error: String,
    val reason: String
)
