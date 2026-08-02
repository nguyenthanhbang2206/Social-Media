package com.nguyenthanhbang.Social_media.dto.response;

import com.nguyenthanhbang.Social_media.common.enumeration.GroupMembershipStatus;
import com.nguyenthanhbang.Social_media.common.enumeration.GroupRole;
import com.nguyenthanhbang.Social_media.common.dto.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class GroupMemberResponse extends BaseResponse {
    private GroupRole role;
    private LocalDateTime joinedAt;
    private Long userId;
    private Long groupId;
    private GroupMembershipStatus status;
    private Boolean isApproved;
    private LocalDateTime requestedAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime leftAt;
}
