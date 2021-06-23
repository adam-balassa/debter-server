package hu.balassa.debter.controller

import hu.balassa.debter.service.DebtService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/room/{roomKey}/debts")
@CrossOrigin("*")
class DebtController(
    val service: DebtService
) {
    @GetMapping
    fun getDebts(@PathVariable roomKey: String) =
        service.getDebts(roomKey)
}