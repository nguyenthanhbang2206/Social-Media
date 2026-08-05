package com.nguyenthanhbang.Social_media.event;

import com.nguyenthanhbang.Social_media.common.event.CommentEvent;
import com.nguyenthanhbang.Social_media.common.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.nguyenthanhbang.Social_media.common.config.RabbitMQConfig.INTERACTION_EXCHANGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentPublisher {
    private final OutboxService outboxService;

    public void publish(CommentEvent event){
        outboxService.saveToOutbox(INTERACTION_EXCHANGE, "comment.created", event);
    }
    public void publishCommentReact(CommentEvent event){
        outboxService.saveToOutbox(INTERACTION_EXCHANGE, "comment.reacted", event);
    }
}
