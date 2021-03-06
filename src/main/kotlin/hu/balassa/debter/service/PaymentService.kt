package hu.balassa.debter.service

import hu.balassa.debter.client.ExchangeClient
import hu.balassa.debter.dto.request.AddPaymentRequest
import hu.balassa.debter.dto.response.GetPaymentResponse
import hu.balassa.debter.dto.response.GetPaymentsResponse
import hu.balassa.debter.dto.response.MemberIncluded
import hu.balassa.debter.dto.response.PaymentResponse
import hu.balassa.debter.dto.response.RoomDetailsResponse
import hu.balassa.debter.mapper.ModelDtoMapper
import hu.balassa.debter.model.Payment
import hu.balassa.debter.repository.DebterRepository
import hu.balassa.debter.util.generateUUID
import hu.balassa.debter.util.loadRoom
import hu.balassa.debter.util.memberIdToName
import hu.balassa.debter.util.paymentsWithMembers
import hu.balassa.debter.util.useRoom
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class PaymentService(
    private val repository: DebterRepository,
    private val mapper: ModelDtoMapper,
    private val exchangeClient: ExchangeClient,
    private val debtService: DebtService
) {
    fun addPayment(request: AddPaymentRequest, roomKey: String) = repository.useRoom(roomKey) { room ->
        val payer = room.members.find { it.id == request.memberId }
            ?: throw IllegalArgumentException("Invalid member id: ${request.memberId}")
        require(request.included.all { included -> room.members.any { it.id == included } }) { "Invalid member id in included" }

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
        fun toResponse(payment: Pair<String, Payment>) = mapper.paymentToGetPaymentResponse(
            payment.second,
            memberIdToName(payment.first, room.members),
            room.members.map { MemberIncluded(memberIdToName(it.id, room.members), it.id in payment.second.includedMemberIds) }
        )
        GetPaymentsResponse(
            paymentsWithMembers(room.members).filter { it.second.active }.map { toResponse(it) },
            paymentsWithMembers(room.members).filter { !it.second.active }.map { toResponse(it) }
        )
    }
}
