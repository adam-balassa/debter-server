package hu.balassa.debter.handler

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import kotlin.reflect.KClass

class Router(private val event: APIGatewayV2HTTPEvent) {
    private val validator = validator()
    private val objectMapper = objectMapper()
    private var response: APIGatewayV2HTTPResponse? = null
    val result get() = response ?: throw IllegalArgumentException("Invalid path ${event.rawPath}")

    fun <RES> get(pattern: String, handleRequest: Request<Any>.() -> RES) =
        matchRequest(pattern, "GET") {
            executeRequest("GET", Any::class, handleRequest)
        }

    inline fun <reified REQ : Any, RES> post(pattern: String, noinline handleRequest: Request<REQ>.() -> RES) =
        matchRequest(pattern, "POST") {
            executeRequest("POST", REQ::class, handleRequest)
        }

    inline fun <reified REQ : Any, RES> put(pattern: String, noinline handleRequest: Request<REQ>.() -> RES) =
        matchRequest(pattern, "PUT") {
            executeRequest("PUT", REQ::class, handleRequest)
        }

    inline fun <reified REQ : Any, RES> patch(pattern: String, noinline handleRequest: Request<REQ>.() -> RES) =
        matchRequest(pattern, "PATCH") {
            executeRequest("PATCH", REQ::class, handleRequest)
        }

    fun <RES> delete(pattern: String, handleRequest: Request<Any>.() -> RES) =
        matchRequest(pattern, "DELETE") {
            executeRequest("DELETE", Any::class, handleRequest)
        }

    fun matchRequest(pattern: String, method: String, callback: () -> APIGatewayV2HTTPResponse) {
        if (response != null) return
        if (event.requestContext.http.method != method) return
        if (!matchRoute(event.requestContext.http.path, pattern)) return
        response = callback()
    }

    fun <T : Any, U> executeRequest(
        method: String,
        requestBodyType: KClass<T>,
        handleRequest: Request<T>.() -> U
    ): APIGatewayV2HTTPResponse {
        val request = Request(
            event.queryStringParameters,
            event.pathParameters,
            event.body,
            requestBodyType
        )
        val result = handleRequest(request)
        return if (result == Unit) sendResponse(204, null)
        else sendResponse(if (method == "POST") 201 else 200, result)
    }

    fun matchRoute(path: String, pattern: String): Boolean {
        val regexPattern = pattern.replace("\\{\\w+}".toRegex(), """([a-zA-Z0-9-]+)""")
        return Regex("^${regexPattern}$").matches(path)
    }


    inner class Request<T : Any>(
        private val queryParams: Map<String, String>?,
        private val pathParams: Map<String, String>?,
        private val bodyString: String?,
        private val bodyType: KClass<T>
    ) {
        val body
            get(): T {
                bodyString ?: throw IllegalArgumentException("Request body is missing")
                return objectMapper.readValue(bodyString, bodyType.java).also {
                    val violations = validator.validate(it)
                    if (violations.isNotEmpty())
                        throw IllegalArgumentException(violations.first().run {
                            val invalidField = propertyPath.joinToString(".")
                            "Invalid field $invalidField: $message"
                        })
                }
            }

        fun pathVariable(key: String) = pathParams?.get(key) ?: throw IllegalArgumentException("$key is missing from path")
        fun queryVariable(key: String) =
            queryParams?.get(key) ?: throw IllegalArgumentException("$key is missing from query")
    }
}