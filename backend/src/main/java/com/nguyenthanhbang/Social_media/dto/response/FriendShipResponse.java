package com.nguyenthanhbang.Social_media.dto.response;

import com.nguyenthanhbang.Social_media.enumeration.FriendShipStatus;
import com.nguyenthanhbang.Social_media.enumeration.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class FriendShipResponse extends BaseResponse{
    private FriendShipStatus status;
    private LocalDateTime acceptedAt;
    private UserResponse sender;
    private UserResponse receiver;
}
