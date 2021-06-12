package hu.balassa.debter.service

import hu.balassa.debter.dto.request.CreateRoomRequest
import hu.balassa.debter.dto.response.CreateRoomResponse
import hu.balassa.debter.mapper.ModelDtoMapper
import hu.balassa.debter.model.Room
import hu.balassa.debter.repository.DebterRepository
import hu.balassa.debter.util.generateRoomKey
import org.springframework.stereotype.Service

@Service
class RoomService(
    private val repository: DebterRepository,
    private val mapper: ModelDtoMapper
) {
    fun createRoom(request: CreateRoomRequest): CreateRoomResponse {
        val usedRoomKeys = repository.findAll().map { it.key }

        val room = Room().apply {
            key = generateRoomKey(usedRoomKeys)
            name = request.name
        }

        repository.save(room)
        return mapper.roomToCreateRoomResponse(room)
    }
}