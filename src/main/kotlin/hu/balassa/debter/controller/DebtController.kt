package hu.balassa.debter.controller

import hu.balassa.debter.handler.Application
import hu.balassa.debter.handler.Router

fun Application.registerDebtController(app: Router) {
    app.get("/room/{roomKey}/debts") {
        debtService.getDebts(pathVariable("roomKey"))
    }
}