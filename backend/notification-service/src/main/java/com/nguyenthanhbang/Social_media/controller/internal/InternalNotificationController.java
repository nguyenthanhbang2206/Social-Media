package com.nguyenthanhbang.Social_media.controller.internal;

import com.nguyenthanhbang.Social_media.common.dto.ApiResponse;
import com.nguyenthanhbang.Social_media.common.enumeration.NotificationType;
import com.nguyenthanhbang.Social_media.dto.request.NotificationRequest;
import com.nguyenthanhbang.Social_media.dto.response.NotificationResponse;
import com.nguyenthanhbang.Social_media.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Internal endpoint for other microservices to create notifications via Feign.
 * This is NOT exposed through the API Gateway.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/internal/notifications")
public class InternalNotificationController {

    private final NotificationService notificationService;

    /**
     * POST /internal/notifications
     * Called by other services (post-service, interaction-service, user-service)
     * to trigger a notification when an event occurs (like, comment, friend request, etc.)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(
            @Valid @RequestBody NotificationRequest request) {
        NotificationResponse notification = notificationService.createNotification(request);
        ApiResponse<NotificationResponse> response = ApiResponse.<NotificationResponse>builder()
                .message("Notification created successfully")
                .status(HttpStatus.CREATED.value())
                .data(notification)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * DELETE /internal/notifications?actorId=1&referenceId=2&type=LIKE
     * Called by other services to remove a notification when an action is undone
     * (e.g., un-like removes the LIKE notification).
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteByReference(
            @RequestParam Long actorId,
            @RequestParam Long referenceId,
            @RequestParam NotificationType type) {
        notificationService.deleteByReference(actorId, referenceId, type);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .message("Notification deleted successfully")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}
