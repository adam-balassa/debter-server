package hu.balassa.debter.mapper

import hu.balassa.debter.dto.response.CreateRoomResponse
import hu.balassa.debter.model.Room
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface ModelDtoMapper {
    @Mappings(
        Mapping(source = "key", target = "roomKey"),
        Mapping(source = "currency", target = "defaultCurrency")
    )
    fun roomToCreateRoomResponse(room: Room): CreateRoomResponse
}