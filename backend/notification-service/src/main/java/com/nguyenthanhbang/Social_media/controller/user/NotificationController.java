package com.nguyenthanhbang.Social_media.controller.user;

import com.nguyenthanhbang.Social_media.common.dto.ApiResponse;
import com.nguyenthanhbang.Social_media.common.enumeration.NotificationType;
import com.nguyenthanhbang.Social_media.dto.request.NotificationRequest;
import com.nguyenthanhbang.Social_media.dto.response.NotificationResponse;
import com.nguyenthanhbang.Social_media.dto.response.UnreadCountResponse;
import com.nguyenthanhbang.Social_media.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

   
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

 
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteByReference(
            @RequestParam("actorId") Long actorId,
            @RequestParam("referenceId") Long referenceId,
            @RequestParam("type") NotificationType type) {
        notificationService.deleteByReference(actorId, referenceId, type);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .message("Notification deleted successfully")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

 
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getMyNotifications(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "20", name = "size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationResponse> notifications = notificationService.getMyNotifications(pageable);
        ApiResponse<Page<NotificationResponse>> response = ApiResponse.<Page<NotificationResponse>>builder()
                .message("Get notifications successfully")
                .status(HttpStatus.OK.value())
                .data(notifications)
                .build();
        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> getUnreadCount() {
        long count = notificationService.getUnreadCount();
        ApiResponse<UnreadCountResponse> response = ApiResponse.<UnreadCountResponse>builder()
                .message("Get unread count successfully")
                .status(HttpStatus.OK.value())
                .data(UnreadCountResponse.builder().unreadCount(count).build())
                .build();
        return ResponseEntity.ok(response);
    }

 
    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable("id") Long id) {
        NotificationResponse notification = notificationService.markAsRead(id);
        ApiResponse<NotificationResponse> response = ApiResponse.<NotificationResponse>builder()
                .message("Notification marked as read")
                .status(HttpStatus.OK.value())
                .data(notification)
                .build();
        return ResponseEntity.ok(response);
    }

    
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        notificationService.markAllAsRead();
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .message("All notifications marked as read")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

   
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable("id") Long id) {
        notificationService.deleteNotification(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .message("Notification deleted successfully")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}
