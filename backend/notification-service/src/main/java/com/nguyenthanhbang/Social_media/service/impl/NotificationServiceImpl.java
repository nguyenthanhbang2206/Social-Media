package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.client.UserClient;
import com.nguyenthanhbang.Social_media.common.dto.UserSummaryResponse;
import com.nguyenthanhbang.Social_media.common.enumeration.NotificationType;
import com.nguyenthanhbang.Social_media.common.util.RequestHeaderUtil;
import com.nguyenthanhbang.Social_media.dto.request.NotificationRequest;
import com.nguyenthanhbang.Social_media.dto.response.NotificationResponse;
import com.nguyenthanhbang.Social_media.mapper.NotificationMapper;
import com.nguyenthanhbang.Social_media.model.Notification;
import com.nguyenthanhbang.Social_media.repository.NotificationRepository;
import com.nguyenthanhbang.Social_media.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserClient userClient;

    @Override
    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        // Don't notify yourself
        if (request.getActorId().equals(request.getRecipientId())) {
            log.debug("Skipping self-notification for userId={}", request.getActorId());
            return null;
        }

        Notification notification = notificationMapper.toNotification(request);
        notification = notificationRepository.save(notification);

        NotificationResponse response = notificationMapper.toNotificationResponse(notification);
        enrichWithActorInfo(response);

        // Push real-time notification via WebSocket
        String destination = "/topic/notifications/" + request.getRecipientId();
        messagingTemplate.convertAndSend(destination, response);
        log.info("Notification sent to userId={} type={}", request.getRecipientId(), request.getType());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(Pageable pageable) {
        Long userId = getCurrentUserId();
        Page<Notification> notifications = notificationRepository
                .findByRecipientIdAndActiveTrueOrderByCreatedDateDesc(userId, pageable);

        return notifications.map(notification -> {
            NotificationResponse response = notificationMapper.toNotificationResponse(notification);
            enrichWithActorInfo(response);
            return response;
        });
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long notificationId) {
        Long userId = getCurrentUserId();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        if (!notification.getRecipientId().equals(userId)) {
            throw new EntityNotFoundException("You do not have permission to access this notification");
        }

        notification.setIsRead(true);
        notification = notificationRepository.save(notification);

        NotificationResponse response = notificationMapper.toNotificationResponse(notification);
        enrichWithActorInfo(response);
        return response;
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        Long userId = getCurrentUserId();
        int updated = notificationRepository.markAllAsReadByRecipientId(userId);
        log.info("Marked {} notifications as read for userId={}", updated, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        Long userId = getCurrentUserId();
        return notificationRepository.countByRecipientIdAndIsReadFalseAndActiveTrue(userId);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {
        Long userId = getCurrentUserId();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        if (!notification.getRecipientId().equals(userId)) {
            throw new EntityNotFoundException("You do not have permission to delete this notification");
        }

        notification.setActive(false);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void deleteByReference(Long actorId, Long referenceId, NotificationType type) {
        int deleted = notificationRepository.softDeleteByActorAndReference(actorId, referenceId, type);
        log.info("Soft-deleted {} notifications for actorId={} referenceId={} type={}", deleted, actorId, referenceId, type);
    }

    // ─── Helper Methods ───────────────────────────────────────────

    private Long getCurrentUserId() {
        return RequestHeaderUtil.getUserId()
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private void enrichWithActorInfo(NotificationResponse response) {
        try {
            UserSummaryResponse actor = userClient.getUserById(response.getActorId()).getData();
            if (actor != null) {
                response.setActorName(actor.getFullName());
                response.setActorAvatar(actor.getAvatar());
            }
        } catch (Exception e) {
            log.warn("Failed to fetch actor info for userId={}: {}", response.getActorId(), e.getMessage());
        }
    }
}
