package hu.balassa.debter

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import hu.balassa.debter.handler.Router
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Playground {
    @Test
    fun sanityCheck() {
        val match = Router(APIGatewayV2HTTPEvent()).matchRoute("/room/BNVOCL/payments/a43922b8-2ecc-4074-9e7f-d7db6632d94f", "/room/{roomKey}/payments/{paymentId}")
        assertThat(match).isTrue
    }
}