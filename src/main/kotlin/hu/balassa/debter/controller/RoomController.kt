package hu.balassa.debter.controller

import hu.balassa.debter.dto.request.AddMemberRequest
import hu.balassa.debter.dto.request.AddMembersRequest
import hu.balassa.debter.dto.request.CreateRoomRequest
import hu.balassa.debter.dto.response.*
import hu.balassa.debter.handler.Router
import hu.balassa.debter.handler.roomService

fun registerRoomController(app: Router) {
    app.get("/room/{roomKey}/details") {
        roomService.getRoomDetails(pathVariable("roomKey"))
    }

    app.get("/room/{roomKey}/summary") {
        roomService.getRoomSummary(pathVariable("roomKey"))
    }

    app.post<CreateRoomRequest, CreateRoomResponse>("/room") {
        roomService.createRoom(body)
    }

    app.post<AddMembersRequest, Unit>("/room/{roomKey}/members") {
        roomService.addMembers(body, pathVariable("roomKey"))
    }

    app.put<AddMemberRequest, Unit>("/room/{roomKey}/members") {
        roomService.addMemberToExistingRoom(pathVariable("roomKey"), body)
    }

    app.delete("/room/{roomKey}/members/{memberId}") {
        roomService.deleteMemberFromRoom(pathVariable("roomKey"), pathVariable("memberId"))
    }

    app.get("/room/{roomKey}/members") {
        roomService.getMembers(pathVariable("roomKey"))
    }

    app.get("/room/{roomKey}/settings") {
        roomService.getRoomSettings(pathVariable("roomKey"))
    }

    app.put<RoomSettings, Unit>("/room/{roomKey}/settings") {
        roomService.updateRoomSettings(pathVariable("roomKey"), body)
    }
}