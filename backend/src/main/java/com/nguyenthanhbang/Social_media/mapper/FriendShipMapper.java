package com.nguyenthanhbang.Social_media.mapper;

import com.nguyenthanhbang.Social_media.dto.response.FriendShipResponse;
import com.nguyenthanhbang.Social_media.model.FriendShip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface FriendShipMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "modifiedDate", source = "modifiedDate")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "modifiedBy", source = "modifiedBy")
    FriendShipResponse toFriendShipResponse(FriendShip friendShip);
    List<FriendShipResponse> toFriendShipResponses(List<FriendShip> friendShips);
}
