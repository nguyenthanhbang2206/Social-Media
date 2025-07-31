package com.nguyenthanhbang.Social_media.dto.response;

import com.nguyenthanhbang.Social_media.enumeration.GroupMembershipStatus;
import com.nguyenthanhbang.Social_media.enumeration.GroupRole;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class GroupMemberResponse extends BaseResponse {
    private GroupRole role;
    private LocalDateTime joinedAt;
    private UserResponse user;
    private GroupMembershipStatus status;
    private Boolean isApproved;
    private LocalDateTime requestedAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime leftAt;
}
