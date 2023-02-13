package hu.balassa.debter.handler

import hu.balassa.debter.dto.request.AddMemberRequest
import hu.balassa.debter.dto.request.AddMembersRequest
import hu.balassa.debter.dto.request.CreateRoomRequest
import hu.balassa.debter.dto.response.*

class GetRoomDetails : LambdaEndpoint<Any, RoomDetailsResponse>(200, endpoint={
    roomService.getRoomDetails(pathVariable("roomKey"))
})

class GetRoomSummary : LambdaEndpoint<Any, RoomSummary>(200, endpoint={
    roomService.getRoomSummary(pathVariable("roomKey"))
})

class CreateRoom : LambdaEndpoint<CreateRoomRequest, CreateRoomResponse>(201, CreateRoomRequest::class.java, endpoint={
    roomService.createRoom(body)
})

class AddMembers : LambdaEndpoint<AddMembersRequest, Unit>(204, AddMembersRequest::class.java, endpoint={
    roomService.addMembers(body, pathVariable("roomKey"))
})

class AddMemberToExistingRoom : LambdaEndpoint<AddMemberRequest, Unit>(204, AddMemberRequest::class.java, endpoint={
    roomService.addMemberToExistingRoom(pathVariable("roomKey"), body)
})

class GetMembers : LambdaEndpoint<Any, List<MemberResponse>>(200, endpoint={
    roomService.getMembers(pathVariable("roomKey"))
})

class GetRoomSettings : LambdaEndpoint<Any, RoomSettings>(200, endpoint={
    roomService.getRoomSettings(pathVariable("roomKey"))
})

class UpdateRoomSettings : LambdaEndpoint<RoomSettings, Unit>(204, RoomSettings::class.java, endpoint={
    roomService.updateRoomSettings(pathVariable("roomKey"), body)
})