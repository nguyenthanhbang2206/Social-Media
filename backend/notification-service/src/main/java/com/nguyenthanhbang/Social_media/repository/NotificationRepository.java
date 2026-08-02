package com.nguyenthanhbang.Social_media.repository;

import com.nguyenthanhbang.Social_media.common.enumeration.NotificationType;
import com.nguyenthanhbang.Social_media.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByRecipientIdAndActiveTrueOrderByCreatedDateDesc(Long recipientId, Pageable pageable);

    long countByRecipientIdAndIsReadFalseAndActiveTrue(Long recipientId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.recipientId = :recipientId AND n.isRead = false AND n.active = true")
    int markAllAsReadByRecipientId(@Param("recipientId") Long recipientId);

    @Modifying
    @Query("UPDATE Notification n SET n.active = false WHERE n.actorId = :actorId AND n.referenceId = :referenceId AND n.type = :type AND n.active = true")
    int softDeleteByActorAndReference(@Param("actorId") Long actorId,
                                       @Param("referenceId") Long referenceId,
                                       @Param("type") NotificationType type);
}
