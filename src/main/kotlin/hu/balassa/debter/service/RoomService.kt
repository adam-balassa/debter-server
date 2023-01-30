package hu.balassa.debter.service

import hu.balassa.debter.client.ExchangeClient
import hu.balassa.debter.dto.request.AddMemberRequest
import hu.balassa.debter.dto.request.AddMembersRequest
import hu.balassa.debter.dto.request.CreateRoomRequest
import hu.balassa.debter.dto.response.*
import hu.balassa.debter.mapper.ModelDtoMapper
import hu.balassa.debter.model.Currency.HUF
import hu.balassa.debter.model.Member
import hu.balassa.debter.model.Room
import hu.balassa.debter.repository.DebterRepository
import hu.balassa.debter.util.*
import java.time.ZonedDateTime

class RoomService(
    private val repository: DebterRepository,
    private val mapper: ModelDtoMapper,
    private val debtService: DebtService,
    private val exchangeClient: ExchangeClient
) {
    fun getRoomDetails(roomKey: String): RoomDetailsResponse = repository.loadRoom(roomKey) { room ->
        mapper.roomToRoomDetailsResponse(room)
    }

    fun createRoom(request: CreateRoomRequest): CreateRoomResponse {
        val room = defaultRoom().apply { name = request.name }
        repository.save(room)
        return mapper.roomToCreateRoomResponse(room)
    }

    fun addMembers(request: AddMembersRequest, roomKey: String) = repository.useRoom(roomKey) { room ->
        room.members = request.members.map { defaultMember().apply { name = it } }
    }

    private fun defaultRoom() = Room().apply {
        currency = HUF
        rounding = 10.0
        members = emptyList()
        lastModified = ZonedDateTime.now()
    }

    private fun defaultMember() = Member().apply {
        id = generateUUID()
        payments = emptyList()
        debts = emptyList()
    }

    fun getRoomSummary(roomKey: String): RoomSummary = repository.loadRoom(roomKey) { room ->
        RoomSummary(
            roomKey,
            room.name,
            room.members.flatMap { member -> member.payments }.filter { it.active }.sumOf { it.convertedValue },
            room.currency,
            room.members.map { MemberSummary(it.name, memberSum(it), memberDebt(it, room.members)) }
        )
    }

    fun getMembers(roomKey: String): List<MemberResponse> = repository.loadRoom(roomKey) { room ->
        room.members.map { MemberResponse(it.id, it.name) }
    }

    fun getRoomSettings(roomKey: String): RoomSettings = repository.loadRoom(roomKey) { room ->
        RoomSettings(room.currency, room.rounding)
    }

    fun updateRoomSettings(roomKey: String, settings: RoomSettings) = repository.useRoom(roomKey) {
        val (currency, rounding) = settings
        if (it.currency != currency) {
            it.currency = currency
            recalculateRealValues(it)
        }
        if (it.rounding != rounding) {
            it.rounding = rounding
        }
        debtService.arrangeDebts(it)
    }

    fun addMemberToExistingRoom(roomKey: String, addMemberRequest: AddMemberRequest) = repository.useRoom(roomKey) { room ->
        val newMember = defaultMember().apply { name = addMemberRequest.name }
        room.members = room.members.toMutableList().apply { add(newMember) }

        val payments = room.members.flatMap { it.payments }
        addMemberRequest.includedPaymentIds.forEach { id ->
            val payment = payments.find { it.id == id } ?: throw IllegalArgumentException("No payment found with id $id")
            payment.includedMemberIds = payment.includedMemberIds.toMutableList().apply { add(newMember.id) }
        }

        debtService.arrangeDebts(room)
    }

    fun deleteMemberFromRoom(roomKey: String, memberId: String) = repository.useRoom(roomKey) { room ->
        if (room.members.any { it.payments.any { payment -> memberId in payment.includedMemberIds } })
            throw IllegalArgumentException("Can't delete member who was included in a payment")
        if (room.members.find { it.id == memberId }?.payments?.isNotEmpty() != false)
            throw IllegalArgumentException("The requested member should exist and should not have payments")

        room.members = room.members.filter { it.id != memberId }
    }

    private fun recalculateRealValues(room: Room) {
        room.members.flatMap { it.payments }.forEach {
            it.convertedValue = exchangeClient.convert(it.currency, room.currency, it.value)
        }
    }
}