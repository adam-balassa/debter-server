package hu.balassa.debter.service

import hu.balassa.debter.dto.request.AddMembersRequest
import hu.balassa.debter.dto.request.CreateRoomRequest
import hu.balassa.debter.dto.response.CreateRoomResponse
import hu.balassa.debter.dto.response.RoomDetailsResponse
import hu.balassa.debter.mapper.ModelDtoMapper
import hu.balassa.debter.model.Currency.HUF
import hu.balassa.debter.model.Member
import hu.balassa.debter.model.Room
import hu.balassa.debter.repository.DebterRepository
import hu.balassa.debter.util.generateRoomKey
import hu.balassa.debter.util.generateUUID
import hu.balassa.debter.util.loadRoom
import hu.balassa.debter.util.useRoom
import org.springframework.stereotype.Service

@Service
class RoomService(
    private val repository: DebterRepository,
    private val mapper: ModelDtoMapper
) {

    fun getRoomDetails(roomKey: String): RoomDetailsResponse = repository.loadRoom(roomKey) { room ->
        mapper.roomToRoomDetailsResponse(room)
    }

    fun createRoom(request: CreateRoomRequest): CreateRoomResponse {
        val usedRoomKeys = repository.findAll().map { it.key }

        val room = defaultRoom().apply {
            key = generateRoomKey(usedRoomKeys)
            name = request.name
        }

        repository.save(room)
        return mapper.roomToCreateRoomResponse(room)
    }

    fun addMembers(request: AddMembersRequest, roomKey: String) = repository.useRoom(roomKey) { room ->
        room.members = request.memberNames.map { defaultMember().apply { name = it } }
    }

    private fun defaultRoom() = Room().apply {
        currency = HUF
        rounding = 10.0
        members = emptyList()
    }

    private fun defaultMember() = Member().apply {
        id = generateUUID()
        payments = emptyList()
        debts = emptySet()
    }
}