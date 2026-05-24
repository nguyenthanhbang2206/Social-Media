package com.nguyenthanhbang.Social_media.dto.request;

import com.nguyenthanhbang.Social_media.common.enumeration.NotificationType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequest {
    @NotNull(message = "Recipient ID is required")
    private Long recipientId;

    @NotNull(message = "Actor ID is required")
    private Long actorId;

    @NotNull(message = "Notification type is required")
    private NotificationType type;

    private Long referenceId;

    private String message;
}
