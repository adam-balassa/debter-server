package hu.balassa.debter.controller

import hu.balassa.debter.handler.Router
import hu.balassa.debter.handler.debtService

fun registerDebtController(app: Router) {
    app.get("/room/{roomKey}/debts") {
        debtService.getDebts(pathVariable("roomKey"))
    }
}