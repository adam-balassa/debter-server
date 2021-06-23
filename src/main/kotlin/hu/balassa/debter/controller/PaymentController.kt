package hu.balassa.debter.controller

import hu.balassa.debter.dto.request.AddPaymentRequest
import hu.balassa.debter.service.PaymentService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/room/{roomKey}/payments")
@CrossOrigin("*")
class PaymentController (
    private val service: PaymentService
) {
    @GetMapping
    fun getPayments(@PathVariable roomKey: String) = service.getPayments(roomKey)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addPayment(
        @PathVariable roomKey: String,
        @RequestBody @Valid request: AddPaymentRequest
    ) = service.addPayment(request, roomKey)


    @PatchMapping("/{paymentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun revivePayment(
        @PathVariable roomKey: String,
        @PathVariable paymentId: String
    ) = service.revivePayment(roomKey, paymentId)


    @DeleteMapping("/{paymentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePayment(
        @PathVariable roomKey: String,
        @PathVariable paymentId: String
    ) = service.deletePayment(roomKey, paymentId)
}