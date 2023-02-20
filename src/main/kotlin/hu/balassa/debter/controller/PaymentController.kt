package hu.balassa.debter.controller

import hu.balassa.debter.dto.request.AddPaymentRequest
import hu.balassa.debter.handler.Application
import hu.balassa.debter.handler.Router

fun Application.registerPaymentController(app: Router) {
    app.get("/room/{roomKey}/payments") {
        paymentService.getPayments(pathVariable("roomKey"))
    }

    app.post<AddPaymentRequest, Unit>("/room/{roomKey}/payments") {
        paymentService.addPayment(body, pathVariable("roomKey"))
    }

    app.patch<Any, Unit>("/room/{roomKey}/payments/{paymentId}") {
        paymentService.revivePayment(pathVariable("roomKey"), pathVariable("paymentId"))
    }

    app.delete("/room/{roomKey}/payments/{paymentId}") {
        paymentService.deletePayment(pathVariable("roomKey"), pathVariable("paymentId"))
    }
}