package hu.balassa.debter.service

import hu.balassa.debter.client.ExchangeClient
import hu.balassa.debter.dto.request.AddPaymentRequest
import hu.balassa.debter.dto.response.GetPaymentsResponse
import hu.balassa.debter.dto.response.MemberShare
import hu.balassa.debter.mapper.ModelDtoMapper
import hu.balassa.debter.model.Payment
import hu.balassa.debter.model.safeSplit
import hu.balassa.debter.repository.DebterRepository
import hu.balassa.debter.util.*
import java.time.ZonedDateTime

class PaymentService(
    private val repository: DebterRepository,
    private val mapper: ModelDtoMapper,
    private val exchangeClient: ExchangeClient,
    private val debtService: DebtService
) {
    fun addPayment(request: AddPaymentRequest, roomKey: String) = repository.useRoom(roomKey) { room ->
        val payer = room.members.find { it.id == request.memberId }
            ?: throw IllegalArgumentException("Invalid member id: ${request.memberId}")
        require(request.split.all {
                splitBetween -> room.members.any { it.id == splitBetween.memberId }
        }) { "Invalid member id in included" }

        val id = generateUUID()
        val convertedValue = exchangeClient.convert(request.currency, room.currency, request.value)
        request.date = request.date ?: ZonedDateTime.now()
        val payment = mapper.addPaymentRequestToPayment(request, id, convertedValue)
        payer.payments = mutableListOf<Payment>().apply { addAll(payer.payments); add(payment) }

        debtService.arrangeDebtForPayment(request, room)
    }

    fun deletePayment(roomKey: String, paymentId: String) = repository.useRoom(roomKey) { room ->
        val payment = room.members.flatMap { it.payments }.find { it.id == paymentId }
            ?: throw IllegalArgumentException("Invalid payment id $paymentId")
        payment.active = false
        debtService.arrangeDebts(room)
    }

    fun revivePayment(roomKey: String, paymentId: String) = repository.useRoom(roomKey) { room ->
        val payment = room.members.flatMap { it.payments }.find { it.id == paymentId }
            ?: throw IllegalArgumentException("Invalid payment id $paymentId")
        payment.active = true
        debtService.arrangeDebts(room)
    }

    fun getPayments(roomKey: String): GetPaymentsResponse = repository.loadRoom(roomKey) { room ->
        fun toResponse(memberId: String, payment: Payment) = mapper.paymentToGetPaymentResponse(
            payment,
            memberIdToName(memberId, room.members),
            shareForMembers(payment.safeSplit, payment.value).map { (memberId, share) ->
                MemberShare(memberIdToName(memberId, room.members), share)
            }
        )
        GetPaymentsResponse(
            paymentsWithMembers(room.members).filter { it.second.active }.map { toResponse(it.first, it.second) },
            paymentsWithMembers(room.members).filter { !it.second.active }.map { toResponse(it.first, it.second) }
        )
    }
}
