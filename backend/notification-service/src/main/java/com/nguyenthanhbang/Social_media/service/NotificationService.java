package com.nguyenthanhbang.Social_media.service;

import com.nguyenthanhbang.Social_media.common.enumeration.NotificationType;
import com.nguyenthanhbang.Social_media.dto.request.NotificationRequest;
import com.nguyenthanhbang.Social_media.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

   
    NotificationResponse createNotification(NotificationRequest request);
    Page<NotificationResponse> getMyNotifications(Pageable pageable);
    NotificationResponse markAsRead(Long notificationId);
    void markAllAsRead();
    long getUnreadCount();
    void deleteNotification(Long notificationId);
    void deleteByReference(Long actorId, Long referenceId, NotificationType type);
}
