package com.nguyenthanhbang.Social_media.common.client;

import com.nguyenthanhbang.Social_media.common.dto.ApiResponse;
import com.nguyenthanhbang.Social_media.common.dto.NotificationEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Shared Feign client for sending notifications from any microservice.
 * Each service that needs to send notifications should include this client.
 *
 * Usage example in interaction-service:
 *   notificationClient.createNotification(NotificationEvent.builder()
 *       .recipientId(postOwnerId)
 *       .actorId(currentUserId)
 *       .type(NotificationType.COMMENT)
 *       .referenceId(postId)
 *       .message("commented on your post")
 *       .build());
 */
@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/api/v1/notifications")
    ApiResponse<?> createNotification(@RequestBody NotificationEvent event);
}
