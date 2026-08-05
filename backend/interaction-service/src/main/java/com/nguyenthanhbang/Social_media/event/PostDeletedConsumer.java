package com.nguyenthanhbang.Social_media.event;

import com.nguyenthanhbang.Social_media.common.event.PostDeletedEvent;
import com.nguyenthanhbang.Social_media.common.util.RabbitConsumerHelper;
import com.nguyenthanhbang.Social_media.service.IdempotencyService;
import com.nguyenthanhbang.Social_media.service.PostLikeService;
import com.nguyenthanhbang.Social_media.service.impl.CommentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.nguyenthanhbang.Social_media.config.RabbitMQConfig.POST_DELETED_QUEUE;
import static com.nguyenthanhbang.Social_media.common.config.RabbitMQConfig.RETRY_EXCHANGE;
import static com.nguyenthanhbang.Social_media.common.config.RabbitMQConfig.DLX_EXCHANGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostDeletedConsumer {

    private final CommentServiceImpl commentService;
    private final PostLikeService postLikeService;
    private final RabbitConsumerHelper rabbitConsumerHelper;
    private final IdempotencyService idempotencyService;

    @Transactional
    @RabbitListener(queues = POST_DELETED_QUEUE)
    public void consumePostDeleted(
            PostDeletedEvent event,
            org.springframework.amqp.core.Message message,
            com.rabbitmq.client.Channel channel
    ) throws java.io.IOException {
        if (event == null || event.getPostId() == null) {
            log.warn("Received empty or invalid PostDeletedEvent");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }

        String eventId = event.getEventId();
        String consumerName = "interaction-service.post-deleted-consumer";

        rabbitConsumerHelper.handleProcessing(
                message,
                channel,
                RETRY_EXCHANGE,
                "post.deleted.retry",
                DLX_EXCHANGE,
                "post.deleted.failed",
                3, // max retries
                () -> {
                    log.info("Received PostDeletedEvent: postId={}, eventId={}", event.getPostId(), eventId);

                    // Check Idempotency
                    if (eventId != null) {
                        if (idempotencyService.isProcessed(eventId, consumerName)) {
                            log.info("Message ID {} was already processed by {}. Skipping.", eventId, consumerName);
                            return;
                        }
                    }

                    Long postId = event.getPostId();
                    commentService.deleteCommentsByPostId(postId);
                    postLikeService.deleteLikesByPostId(postId);
                    log.info("Successfully cleaned up comments and likes for postId={}", event.getPostId());

                    // Mark as processed in database
                    if (eventId != null) {
                        idempotencyService.markAsProcessed(eventId, consumerName);
                    }
                }
        );
    }
}
