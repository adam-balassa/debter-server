package hu.balassa.debter.handler

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import hu.balassa.debter.controller.registerDebtController
import hu.balassa.debter.controller.registerPaymentController
import hu.balassa.debter.controller.registerRoomController
import hu.balassa.debter.exception.withErrorHandling

class Handler: RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
    var app = Application()

    override fun handleRequest(event: APIGatewayV2HTTPEvent, context: Context?): APIGatewayV2HTTPResponse =
        withErrorHandling {
            log.info("${event.rawPath} $event")
            app.apply { init() }
            Router(event).let {
                app.registerRoomController(it)
                app.registerPaymentController(it)
                app.registerDebtController(it)
                it.result
            }
        }
}
