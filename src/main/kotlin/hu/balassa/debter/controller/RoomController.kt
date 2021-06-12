package hu.balassa.debter.controller

import hu.balassa.debter.dto.request.CreateRoomRequest
import hu.balassa.debter.dto.response.CreateRoomResponse
import hu.balassa.debter.model.Currency
import hu.balassa.debter.service.RoomService
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/room")
class RoomController(
    private val service: RoomService
) {

    @PostMapping
    @ResponseStatus(CREATED)
    fun createRoom(@RequestBody @Valid request: CreateRoomRequest) =
        service.createRoom(request)
}