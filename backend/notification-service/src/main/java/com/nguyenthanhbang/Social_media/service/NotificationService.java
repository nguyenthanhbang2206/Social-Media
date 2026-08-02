package com.nguyenthanhbang.Social_media.service;

import com.nguyenthanhbang.Social_media.common.enumeration.NotificationType;
import com.nguyenthanhbang.Social_media.dto.request.NotificationRequest;
import com.nguyenthanhbang.Social_media.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    /**
     * Create a new notification and push it via WebSocket in real-time.
     */
    NotificationResponse createNotification(NotificationRequest request);

    /**
     * Get paginated notifications for the current authenticated user.
     */
    Page<NotificationResponse> getMyNotifications(Pageable pageable);

    /**
     * Mark a single notification as read.
     */
    NotificationResponse markAsRead(Long notificationId);

    /**
     * Mark all notifications as read for the current user.
     */
    void markAllAsRead();

    /**
     * Get count of unread notifications for the current user.
     */
    long getUnreadCount();

    /**
     * Delete a notification (soft delete via BaseEntity active flag).
     */
    void deleteNotification(Long notificationId);

    /**
     * Soft-delete notification by actor, reference and type.
     * Used for undo operations (e.g., un-like removes the like notification).
     */
    void deleteByReference(Long actorId, Long referenceId, NotificationType type);
}
