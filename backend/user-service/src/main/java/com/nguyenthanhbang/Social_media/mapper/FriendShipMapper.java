package com.nguyenthanhbang.Social_media.mapper;

import com.nguyenthanhbang.Social_media.dto.response.FriendShipResponse;
import com.nguyenthanhbang.Social_media.model.FriendShip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FriendShipMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "modifiedDate", source = "modifiedDate")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "modifiedBy", source = "modifiedBy")
    @Mapping(target = "senderId", source = "senderId")
    @Mapping(target = "receiverId", source = "receiverId")
    FriendShipResponse toFriendShipResponse(FriendShip friendShip);
    List<FriendShipResponse> toFriendShipResponses(List<FriendShip> friendShips);
}
