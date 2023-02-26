package hu.balassa.debter.mapper

import hu.balassa.debter.dto.request.AddPaymentRequest
import hu.balassa.debter.dto.response.*
import hu.balassa.debter.model.*
import hu.balassa.debter.util.paymentsWithMembers
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.Named

@Mapper
interface ModelDtoMapper {
    @Mapping(source = "key", target = "roomKey")
    fun roomToCreateRoomResponse(room: Room): CreateRoomResponse

    @Mappings(
        Mapping(source = "members", target = "payments", qualifiedByName = ["membersToPayments"]),
        Mapping(source = "members", target = "debts", qualifiedByName = ["membersToDebts"]),
        Mapping(source = "key", target = "roomKey"),
        Mapping(source = "currency", target = "defaultCurrency")
    )
    fun roomToRoomDetailsResponse(room: Room): RoomDetailsResponse

    @Mappings(Mapping(target = "active", expression = "java(true)"))
    fun addPaymentRequestToPayment(addPaymentRequest: AddPaymentRequest, id: String, convertedValue: Double): Payment

    @Named("membersToPayments")
    @JvmDefault
    fun membersToPaymentResponse(members: List<Member>): List<PaymentResponse> =
        paymentsWithMembers(members).map { paymentToPaymentResponse(it.second, it.first) }

    @Named("membersToDebts")
    @JvmDefault
    fun membersToDebtResponse(members: List<Member>): List<DebtResponse> =
        members.flatMap { it.debts.map { debt -> it.id to debt } }
            .map { debtArrangementToDebtResponse(it.second, it.first) }

    @Mappings(Mapping(source = "payment.convertedValue", target = "realValue"))
    fun paymentToPaymentResponse(payment: Payment, memberId: String): PaymentResponse

    @Mappings(Mapping(source = "split", target = "split"))
    fun paymentToGetPaymentResponse(payment: Payment, memberName: String, split: List<MemberShare>): GetPaymentResponse

    @Mappings(Mapping(source = "debt.payeeId", target = "to"))
    fun debtArrangementToDebtResponse(debt: DebtArrangement, from: String): DebtResponse
}