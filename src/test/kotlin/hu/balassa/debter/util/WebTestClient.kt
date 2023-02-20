package hu.balassa.debter.util

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent.RequestContext
import hu.balassa.debter.handler.Handler
import hu.balassa.debter.handler.log
import hu.balassa.debter.handler.objectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat

class WebTestClient(private val handler: Handler) {
    val mapper = objectMapper()

    private var method: String = "GET"
    private var path: String = "/"
    private var body: String? = null
    private var pathParams: MutableMap<String, String> = mutableMapOf()

    var responseString: String? = null
    private var responseStatus: Int? = null


    fun get(): WebTestClient { method = "GET"; return this }
    fun post(): WebTestClient { method = "POST"; return this }
    fun put(): WebTestClient { method = "PUT"; return this }
    fun delete(): WebTestClient { method = "DELETE"; return this }
    fun pattern(uri: String): WebTestClient { path = "/$uri"; return this }
    fun pathParam(key: String, value: String): WebTestClient {
        pathParams[key] = value
        path = path.replace("{$key}", value)
        return this
    }
    fun bodyValue(bodyValue: Any): WebTestClient {
        body = mapper.writeValueAsString(bodyValue)
        return this
    }
    fun exchange(): WebTestClient {
        val event = APIGatewayV2HTTPEvent
            .builder()
            .withRawPath(path)
            .withRequestContext(
                RequestContext
                .builder()
                .withHttp(RequestContext.Http.builder().withMethod(method).withPath(path).build())
                .build()
            )
            .withPathParameters(pathParams)
            .withBody(body)
            .build()
        log.debug("Request: $method $path")
        val response = handler.handleRequest(event, null)
        responseString = response.body
        responseStatus = response.statusCode
        return this
    }

    fun expectStatus(): AssertStatus = object: AssertStatus {
        override val isOk: WebTestClient get() = checkStatus(200)
        override val isCreated: WebTestClient get() = checkStatus(201)
        override val isNoContent: WebTestClient get() = checkStatus(204)
        override val isBadRequest: WebTestClient get() = checkStatus(400)
        override val isNotFound: WebTestClient get() = checkStatus(404)


        private fun checkStatus(status: Int): WebTestClient {
            assertThat(responseStatus).isEqualTo(status);
            return this@WebTestClient
        }
    }

    inline fun <reified T> responseBody(): T {
        return mapper.readValue(responseString
            ?: throw AssertionError("Response body was empty"))
    }

    fun expectBody(): AssertBody = object: AssertBody {
        override val isEmpty: Unit get() { assertThat(responseString).isNullOrEmpty() }
    }

    interface AssertStatus {
        val isOk: WebTestClient
        val isCreated: WebTestClient
        val isNoContent: WebTestClient
        val isBadRequest: WebTestClient
        val isNotFound: WebTestClient
    }

    interface AssertBody {
        val isEmpty: Unit
    }
}