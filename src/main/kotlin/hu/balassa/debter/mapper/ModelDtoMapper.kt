package hu.balassa.debter.mapper

import hu.balassa.debter.dto.request.AddPaymentRequest
import hu.balassa.debter.dto.response.CreateRoomResponse
import hu.balassa.debter.dto.response.DebtResponse
import hu.balassa.debter.dto.response.GetPaymentResponse
import hu.balassa.debter.dto.response.MemberIncluded
import hu.balassa.debter.dto.response.MemberResponse
import hu.balassa.debter.dto.response.PaymentResponse
import hu.balassa.debter.dto.response.RoomDetailsResponse
import hu.balassa.debter.model.DebtArrangement
import hu.balassa.debter.model.Member
import hu.balassa.debter.model.Payment
import hu.balassa.debter.model.Room
import hu.balassa.debter.util.memberDebt
import hu.balassa.debter.util.memberSum
import hu.balassa.debter.util.paymentsWithMembers
import org.mapstruct.IterableMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.Named

@Mapper(componentModel = "spring", )
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

    @Mappings(
        Mapping(source = "addPaymentRequest.included", target = "includedMemberIds"),
        Mapping(target = "active", expression = "java(true)")
    )
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

    @Mappings(
        Mapping(source = "payment.convertedValue", target = "realValue"),
        Mapping(source = "payment.includedMemberIds", target = "included")
    )
    fun paymentToPaymentResponse(payment: Payment, memberId: String): PaymentResponse

    fun paymentToGetPaymentResponse(payment: Payment, memberName: String, includedMembers: List<MemberIncluded>): GetPaymentResponse

    @Mappings(
        Mapping(source = "debt.payeeId", target = "to")
    )
    fun debtArrangementToDebtResponse(debt: DebtArrangement, from: String): DebtResponse
}