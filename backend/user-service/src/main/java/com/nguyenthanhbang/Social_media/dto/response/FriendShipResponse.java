package com.nguyenthanhbang.Social_media.dto.response;

import com.nguyenthanhbang.Social_media.common.enumeration.FriendShipStatus;
import com.nguyenthanhbang.Social_media.common.dto.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class FriendShipResponse extends BaseResponse{
    private FriendShipStatus status;
    private LocalDateTime acceptedAt;
    private Long senderId;
    private Long receiverId;
}
