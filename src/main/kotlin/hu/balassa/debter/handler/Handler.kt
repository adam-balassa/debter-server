package hu.balassa.debter.handler

import hu.balassa.debter.dto.response.RoomDetailsResponse

class GetRoomDetails : LambdaEndpoint<Unit, RoomDetailsResponse>(200, endpoint={
    roomService.getRoomDetails(it.pathParams["roomKey"]!!)
})