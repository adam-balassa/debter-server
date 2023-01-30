package hu.balassa.debter.handler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.nio.charset.StandardCharsets
import java.util.*

class ApiGatewayResponse(
    val statusCode: Int,
    val body: String?,
    val headers: Map<String, String>,
) {
    companion object {
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    class Builder {
        private val objectMapper = jacksonObjectMapper()

        var statusCode: Int = 200
        var rawBody: String? = null
        var headers: Map<String, String> = Collections.emptyMap()
        var objectBody: Any? = null
        var binaryBody: ByteArray? = null

        fun build() = ApiGatewayResponse(statusCode, when {
            rawBody != null -> rawBody as String
            objectBody != null -> objectMapper.writeValueAsString(objectBody)
            binaryBody != null -> String(Base64.getEncoder().encode(binaryBody), StandardCharsets.UTF_8)
            else -> null
        }, headers)
    }
}