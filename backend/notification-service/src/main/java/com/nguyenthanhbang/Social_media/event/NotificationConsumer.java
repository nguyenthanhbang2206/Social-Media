package com.nguyenthanhbang.Social_media.event;

import com.nguyenthanhbang.Social_media.client.UserClient;
import com.nguyenthanhbang.Social_media.common.enumeration.NotificationType;
import com.nguyenthanhbang.Social_media.common.event.CommentEvent;
import com.nguyenthanhbang.Social_media.common.event.FriendEvent;
import com.nguyenthanhbang.Social_media.common.event.GroupEvent;
import com.nguyenthanhbang.Social_media.common.event.PostReactedEvent;
import com.nguyenthanhbang.Social_media.dto.request.NotificationRequest;
import com.nguyenthanhbang.Social_media.service.NotificationService;
import com.nguyenthanhbang.Social_media.service.IdempotencyService;
import com.nguyenthanhbang.Social_media.common.util.RabbitConsumerHelper;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static com.nguyenthanhbang.Social_media.common.config.RabbitMQConfig.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationService notificationService;
    private final UserClient userClient;
    private final RabbitConsumerHelper rabbitConsumerHelper;
    private final IdempotencyService idempotencyService;

    @Transactional
    @RabbitListener(queues = POST_NOTIFICATION_QUEUE)
    public void consumePost(PostReactedEvent event, Message message, Channel channel) throws IOException {
        String eventId = event.getEventId();
        String consumerName = "notification-service.post-reacted-consumer";

        rabbitConsumerHelper.handleProcessing(
                message,
                channel,
                RETRY_EXCHANGE,
                "post-reacted.retry",
                DLX_EXCHANGE,
                "post-reacted.failed",
                3,
                ()->{
                    if (event == null || event.getActorId() == null || event.getOwnerId() == null) {
                        log.warn("Skip notification: missing actorId/ownerId for event={}", event);
                        return;
                    }

                    // Check Idempotency
                    if (eventId != null) {
                        if (idempotencyService.isProcessed(eventId, consumerName)) {
                            log.info("Message ID {} was already processed by {}. Skipping.", eventId, consumerName);
                            return;
                        }
                    }

                    if (event.getActorId().equals(event.getOwnerId())) {
                        log.info("Skip self-notification for post reaction: actorId={}, ownerId={}", event.getActorId(), event.getOwnerId());
                        if (eventId != null) {
                            idempotencyService.markAsProcessed(eventId, consumerName);
                        }
                        return;
                    }

                    String reactionText = switch (event.getReactionType()) {
                        case LIKE -> "thích";
                        case LOVE -> "yêu thích";
                        case HAHA -> "haha";
                        case WOW -> "wow";
                        case SAD -> "buồn";
                        case ANGRY -> "phẫn nộ";
                    };

                    NotificationRequest request = NotificationRequest.builder()
                            .type(NotificationType.REACT)
                            .recipientId(event.getOwnerId())
                            .actorId(event.getActorId())
                            .referenceId(event.getPostId())
                            .message(event.getActorName() + " đã "+ reactionText + " bài viết của bạn")
                            .build();

                    notificationService.createNotification(request);

                    // Mark as processed in database
                    if (eventId != null) {
                        idempotencyService.markAsProcessed(eventId, consumerName);
                    }
                    log.info("notification created {}", request);
                }
        );
    }
    
    @Transactional
    @RabbitListener(queues = COMMENT_NOTIFICATION_QUEUE)
    public void consumeComment(CommentEvent event, Message message, Channel channel) throws java.io.IOException {
        String eventId = event.getEventId();
        String consumerName = "notification-service.comment-consumer";

        rabbitConsumerHelper.handleProcessing(
                message,
                channel,
                RETRY_EXCHANGE,
                "comment.retry",
                DLX_EXCHANGE,
                "comment.failed",
                3, 
                () -> {
                    if (event == null || event.getAction() == null || event.getRecipientId() == null || event.getActorId() == null) {
                        log.warn("Skip notification: missing action/recipientId/actorId for event={}", event);
                        return;
                    }

                    log.info("-----------consume event: {}", event.toString());

                    // Check Idempotency
                    if (eventId != null) {
                        if (idempotencyService.isProcessed(eventId, consumerName)) {
                            log.info("Message ID {} was already processed by {}. Skipping.", eventId, consumerName);
                            return;
                        }
                    }

                    if (event.getActorId().equals(event.getRecipientId())) {
                        log.info("Skip self-notification for comment: actorId={}, recipientId={}", event.getActorId(), event.getRecipientId());
                        if (eventId != null) {
                            idempotencyService.markAsProcessed(eventId, consumerName);
                        }
                        return;
                    }

                    String type = "";
                    if (event.getAction().equals("REACT")) {
                        type = "bày tỏ cảm xúc về bình luận: " + event.getCommentPreview();
                    } else {
                        type = event.isReply() ? "trả lời bình luận: " + event.getCommentPreview() : "comment bài viết của bạn";
                    }
                    NotificationRequest request = NotificationRequest.builder()
                            .type(NotificationType.COMMENT)
                            .recipientId(event.getRecipientId())
                            .actorId(event.getActorId())
                            .referenceId(event.getPostId())
                            .message(event.getActorName() + " đã " + type)
                            .build();

                    notificationService.createNotification(request);

                    // Mark as processed in database
                    if (eventId != null) {
                        idempotencyService.markAsProcessed(eventId, consumerName);
                    }
                    log.info("notification created {}", request);
                }
        );
    }

    @Transactional
    @RabbitListener(queues = FRIEND_NOTIFICATION_QUEUE)
    public void consumeFriend(FriendEvent event, Message message, Channel channel) throws IOException {
        String eventId = event.getEventId();
        String consumerName = "notification-service.friend-consumer";

        rabbitConsumerHelper.handleProcessing(message,
                channel,
        RETRY_EXCHANGE,
                "friend.retry",
                DLX_EXCHANGE,
                "friend.failed",
                3,
                ()->{
                    if (event == null || event.getAction() == null || event.getRecipientId() == null || event.getActorId() == null) {
                        log.warn("Skip notification: missing action/recipientId/actorId for event={}", event);
                        return;
                    }

                    // Check Idempotency
                    if (eventId != null) {
                        if (idempotencyService.isProcessed(eventId, consumerName)) {
                            log.info("Message ID {} was already processed by {}. Skipping.", eventId, consumerName);
                            return;
                        }
                    }

                    log.info("-----------consume event: {}", event.toString());

                    if (event.getActorId().equals(event.getRecipientId())) {
                        log.info("Skip self-notification for friend event: actorId={}, recipientId={}", event.getActorId(), event.getRecipientId());
                        if (eventId != null) {
                            idempotencyService.markAsProcessed(eventId, consumerName);
                        }
                        return;
                    }

                    String type = event.getAction().equals("REQUESTED") ? " gửi lời mời kết bạn" : " chấp nhận yêu cầu kết bạn";
                    NotificationRequest request = NotificationRequest.builder()
                            .type(event.getAction().equals("REQUESTED") ? NotificationType.FRIEND_REQUEST : NotificationType.FRIEND_ACCEPT)
                            .recipientId(event.getRecipientId())
                            .actorId(event.getActorId())
                            .message(event.getActorName() + " đã"+ type)
                            .build();
                    notificationService.createNotification(request);

                    // Mark as processed in database
                    if (eventId != null) {
                        idempotencyService.markAsProcessed(eventId, consumerName);
                    }
                    log.info("notification created {}", request);
                }
        );
    }

    @Transactional
    @RabbitListener(queues = GROUP_NOTIFICATION_QUEUE)
    public void consumeGroup(GroupEvent event, Message message, Channel channel) throws IOException {
        String eventId = event.getEventId();
        String consumerName = "notification-service.group-consumer";

        rabbitConsumerHelper.handleProcessing(message,
                channel,
                RETRY_EXCHANGE,
                "group.retry",
                DLX_EXCHANGE,
                "group.failed",
                3,
                ()->{
                    if (event == null || event.getEventType() == null || event.getRecipientId() == null || event.getActorId() == null) {
                        log.warn("Skip notification: missing eventType/recipientId/actorId for event={}", event);
                        return;
                    }

                    // Check Idempotency
                    if (eventId != null) {
                        if (idempotencyService.isProcessed(eventId, consumerName)) {
                            log.info("Message ID {} was already processed by {}. Skipping.", eventId, consumerName);
                            return;
                        }
                    }

                    log.info("-----------consume event: {}", event.toString());

                    if (event.getActorId().equals(event.getRecipientId())) {
                        log.info("Skip self-notification for group event: actorId={}, recipientId={}", event.getActorId(), event.getRecipientId());
                        if (eventId != null) {
                            idempotencyService.markAsProcessed(eventId, consumerName);
                        }
                        return;
                    }

                    GroupEvent.GroupEventType eventType = event.getEventType();

                    switch (eventType) {
                        case APPROVED:
                            log.info("send notification for group approved");
                            NotificationRequest approved = NotificationRequest.builder()
                                    .message("Bạn vừa tham gia nhóm " + event.getName())
                                    .recipientId(event.getRecipientId())
                                    .type(NotificationType.GROUP_APPROVED)
                                    .actorId(event.getActorId())
                                    .build();
                            notificationService.createNotification(approved);
                            break;

                        case REQUESTED:
                            log.info("send notification for group request");
                            NotificationRequest request = NotificationRequest.builder()
                                    .message(event.getActorName() + " yêu cầu tham gia nhóm")
                                    .recipientId(event.getRecipientId())
                                    .type(NotificationType.GROUP_REQUESTED)
                                    .actorId(event.getActorId())
                                    .build();
                            notificationService.createNotification(request);
                            break;

                        default:
                            log.warn("Unknown or unhandled group event type: {}", eventType);
                            break;
                    }

                    // Mark as processed in database
                    if (eventId != null) {
                        idempotencyService.markAsProcessed(eventId, consumerName);
                    }
                }
        );
    }
}
