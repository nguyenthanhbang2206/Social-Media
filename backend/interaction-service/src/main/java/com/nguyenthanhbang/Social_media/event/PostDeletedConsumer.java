package com.nguyenthanhbang.Social_media.event;

import com.nguyenthanhbang.Social_media.common.event.PostDeletedEvent;
import com.nguyenthanhbang.Social_media.service.PostLikeService;
import com.nguyenthanhbang.Social_media.service.impl.CommentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static com.nguyenthanhbang.Social_media.config.RabbitMQConfig.POST_DELETED_QUEUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostDeletedConsumer {

    private final CommentServiceImpl commentService;
    private final PostLikeService postLikeService;

    @RabbitListener(queues = POST_DELETED_QUEUE)
    public void consumePostDeleted(PostDeletedEvent event){
        if (event == null || event.getPostId() == null) {
            log.warn("Received empty or invalid PostDeletedEvent");
            return;
        }
        log.info("Received PostDeletedEvent: postId={}", event.getPostId());
        try {
            Long postId = event.getPostId();
            commentService.deleteCommentsByPostId(postId);
            postLikeService.deleteLikesByPostId(postId);
            log.info("Successfully cleaned up comments and likes for postId={}", event.getPostId());
        }catch (Exception e){
            log.error("Error cleaning up post interactions for postId={}", event.getPostId(), e);
        }
    }
}
