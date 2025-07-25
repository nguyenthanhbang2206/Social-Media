package com.nguyenthanhbang.Social_media.dto.response;

import com.nguyenthanhbang.Social_media.enumeration.FriendShipStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FriendShipResponse extends BaseResponse{
    private FriendShipStatus status;
    private LocalDateTime acceptedAt;
    private UserResponse sender;
    private UserResponse receiver;
}
