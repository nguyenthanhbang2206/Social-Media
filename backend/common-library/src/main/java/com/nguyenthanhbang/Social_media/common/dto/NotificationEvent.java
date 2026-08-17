package com.nguyenthanhbang.Social_media.common.dto;

import com.nguyenthanhbang.Social_media.common.enumeration.NotificationType;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent {
    private Long recipientId;
    private Long actorId;
    private NotificationType type;
    private Long referenceId;
    private String message;
}
