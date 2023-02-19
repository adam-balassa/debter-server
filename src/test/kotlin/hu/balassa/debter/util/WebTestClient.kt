package hu.balassa.debter.util

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent.RequestContext
import hu.balassa.debter.handler.Handler
import hu.balassa.debter.handler.log
import hu.balassa.debter.handler.objectMapper
import org.assertj.core.api.Assertions.assertThat

class WebTestClient {
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
            .build()
        log.debug("Request: $method $path")
        val response = Handler().handleRequest(event, null)
        responseString = response.body
        responseStatus = response.statusCode
        return this
    }

    fun expectStatus(expectedStatus: Int): WebTestClient {
        assertThat(responseStatus).isEqualTo(expectedStatus)
        return this
    }

    inline fun <reified T> responseBody(): T {
        return mapper.readValue(responseString, T::class.java)
    }
}