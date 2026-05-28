package com.nguyenthanhbang.Social_media.event;

import com.nguyenthanhbang.Social_media.client.UserClient;
import com.nguyenthanhbang.Social_media.common.enumeration.NotificationType;
import com.nguyenthanhbang.Social_media.common.event.CommentEvent;
import com.nguyenthanhbang.Social_media.common.event.FriendEvent;
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

    @RabbitListener(queues = POST_NOTIFICATION_QUEUE)
    public void consumePost(PostReactedEvent event){
        log.info("-----------consume event: {}", event.toString());

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
                .message(event.getActorName() + "đã"+ type)
                .build();
        notificationService.createNotification(request);
        log.info("notification created {}", request);
    }
    @RabbitListener(queues = FRIEND_NOTIFICATION_QUEUE)
    public void consumeFriend(FriendEvent event){
        log.info("-----------consume event: {}", event.toString());

        String type = event.getAction().equals("REQUEST") ? " gửi lời mời kết bạn" : " chấp nhận yêu cầu kết bạn";
        NotificationRequest request = NotificationRequest.builder()
                .type(event.getAction().equals("REQUEST") ? NotificationType.FRIEND_REQUEST : NotificationType.FRIEND_ACCEPT)
                .recipientId(event.getRecipientId())
                .actorId(event.getActorId())
                .message(event.getActorName() + " đã"+ type)
                .build();
        notificationService.createNotification(request);
        log.info("notification created {}", request);
    }
}
