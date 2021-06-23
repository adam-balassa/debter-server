package hu.balassa.debter.controller

import hu.balassa.debter.dto.request.AddMemberRequest
import hu.balassa.debter.dto.request.AddMembersRequest
import hu.balassa.debter.dto.request.CreateRoomRequest
import hu.balassa.debter.dto.response.RoomSettings
import hu.balassa.debter.service.RoomService
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/room")
@CrossOrigin("*")
class RoomController(
    private val service: RoomService
) {

    @GetMapping("/{roomKey}")
    fun getRoomDetails(@PathVariable roomKey: String) =
        service.getRoomDetails(roomKey)

    @GetMapping("/{roomKey}/summary")
    fun getRoomSummary(@PathVariable roomKey: String) =
        service.getRoomSummary(roomKey)

    @PostMapping
    @ResponseStatus(CREATED)
    fun createRoom(@RequestBody @Valid request: CreateRoomRequest) =
        service.createRoom(request)

    @PostMapping("/{roomKey}/members")
    @ResponseStatus(NO_CONTENT)
    fun addMembers(@RequestBody @Valid request: AddMembersRequest, @PathVariable roomKey: String) =
        service.addMembers(request, roomKey)

    @PutMapping("/{roomKey}/members")
    @ResponseStatus(NO_CONTENT)
    fun addMemberToExistingRoom(@RequestBody newMember: AddMemberRequest, @PathVariable roomKey: String) =
        service.addMemberToExistingRoom(roomKey, newMember)

    @DeleteMapping("/{roomKey}/members/{memberId}")
    @ResponseStatus(NO_CONTENT)
    fun deleteMemberFromRoom(@PathVariable roomKey: String, @PathVariable memberId: String) =
        service.deleteMemberFromRoom(roomKey, memberId)

    @GetMapping("/{roomKey}/members")
    fun getMembers(@PathVariable roomKey: String) =
        service.getMembers(roomKey)

    @GetMapping("/{roomKey}/settings")
    fun getRoomSettings(@PathVariable roomKey: String) =
        service.getRoomSettings(roomKey)

    @PutMapping("/{roomKey}/settings")
    @ResponseStatus(NO_CONTENT)
    fun updateRoomSettings(@RequestBody settings: RoomSettings, @PathVariable roomKey: String) =
        service.updateRoomSettings(roomKey, settings)
}