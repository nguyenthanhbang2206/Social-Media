package com.nguyenthanhbang.Social_media.dto.response;

import com.nguyenthanhbang.Social_media.common.dto.BaseResponse;
import com.nguyenthanhbang.Social_media.common.enumeration.NotificationType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationResponse extends BaseResponse {
    private Long recipientId;
    private Long actorId;
    private NotificationType type;
    private Long referenceId;
    private String message;
    private Boolean isRead;
    private String actorName;
    private String actorAvatar;
}
