package com.nguyenthanhbang.Social_media.event;

import com.nguyenthanhbang.Social_media.client.UserClient;
import com.nguyenthanhbang.Social_media.common.enumeration.NotificationType;
import com.nguyenthanhbang.Social_media.common.event.CommentEvent;
import com.nguyenthanhbang.Social_media.common.event.FriendEvent;
import com.nguyenthanhbang.Social_media.common.event.GroupEvent;
import com.nguyenthanhbang.Social_media.common.event.PostReactedEvent;
import com.nguyenthanhbang.Social_media.dto.request.NotificationRequest;
import com.nguyenthanhbang.Social_media.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static com.nguyenthanhbang.Social_media.common.config.RabbitMQConfig.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationService notificationService;
    private final UserClient userClient;
    int count=1;
    @RabbitListener(queues = POST_NOTIFICATION_QUEUE)
    public void consumePost(PostReactedEvent event){
        if (event == null || event.getActorId() == null || event.getOwnerId() == null) {
            log.warn("Skip notification: missing actorId/ownerId for event={}", event);
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
        log.info("notification created {}", request);
    }
    @RabbitListener(queues = COMMENT_NOTIFICATION_QUEUE)
    public void consumeComment(CommentEvent event){
        log.info("-----------consume event: {}", event.toString());
        String type = "";
        if(event.getAction().equals("REACT")){
            type = "bày tỏ cảm xúc về bình luận: " + event.getCommentPreview();
        }else {
            type = event.isReply() ? "trả lời bình luận: " + event.getCommentPreview(): "comment bài viết của bạn";
        }
        NotificationRequest request = NotificationRequest.builder()
                .type(NotificationType.COMMENT)
                .recipientId(event.getRecipientId())
                .actorId(event.getActorId())
                .referenceId(event.getPostId())
                .message(event.getActorName() + " đã "+ type)
                .build();
        notificationService.createNotification(request);
        log.info("notification created {}", request);
    }
    @RabbitListener(queues = FRIEND_NOTIFICATION_QUEUE)
    public void consumeFriend(FriendEvent event){
        log.info("-----------consume event: {}", event.toString());

        String type = event.getAction().equals("REQUESTED") ? " gửi lời mời kết bạn" : " chấp nhận yêu cầu kết bạn";
        NotificationRequest request = NotificationRequest.builder()
                .type(event.getAction().equals("REQUESTED") ? NotificationType.FRIEND_REQUEST : NotificationType.FRIEND_ACCEPT)
                .recipientId(event.getRecipientId())
                .actorId(event.getActorId())
                .message(event.getActorName() + " đã"+ type)
                .build();
        notificationService.createNotification(request);
        log.info("notification created {}", request);
    }

    @RabbitListener(queues = GROUP_NOTIFICATION_QUEUE)
    public void consumeGroup(GroupEvent event){
        log.info("-----------consume event: {}", event.toString());
        GroupEvent.GroupEventType eventType = event.getEventType();

        if (eventType == null) {
            return;
        }

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
                throw new IllegalArgumentException("Loại event không hợp lệ");
        }
    }
}
