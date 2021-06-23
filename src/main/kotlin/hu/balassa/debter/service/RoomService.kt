package hu.balassa.debter.service

import hu.balassa.debter.dto.request.AddMembersRequest
import hu.balassa.debter.dto.request.CreateRoomRequest
import hu.balassa.debter.dto.response.CreateRoomResponse
import hu.balassa.debter.dto.response.MemberResponse
import hu.balassa.debter.dto.response.MemberSummary
import hu.balassa.debter.dto.response.RoomDetailsResponse
import hu.balassa.debter.dto.response.RoomSummary
import hu.balassa.debter.mapper.ModelDtoMapper
import hu.balassa.debter.model.Currency.HUF
import hu.balassa.debter.model.Member
import hu.balassa.debter.model.Room
import hu.balassa.debter.repository.DebterRepository
import hu.balassa.debter.util.generateRoomKey
import hu.balassa.debter.util.generateUUID
import hu.balassa.debter.util.loadRoom
import hu.balassa.debter.util.logger
import hu.balassa.debter.util.memberDebt
import hu.balassa.debter.util.memberSum
import hu.balassa.debter.util.useRoom
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class RoomService(
    private val repository: DebterRepository,
    private val mapper: ModelDtoMapper
) {

    private val log = logger<RoomService>()

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
}