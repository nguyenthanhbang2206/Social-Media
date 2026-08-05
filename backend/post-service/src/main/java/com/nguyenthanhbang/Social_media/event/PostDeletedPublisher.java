package com.nguyenthanhbang.Social_media.event;

import com.nguyenthanhbang.Social_media.common.event.PostDeletedEvent;
import com.nguyenthanhbang.Social_media.common.outbox.service.OutboxService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.nguyenthanhbang.Social_media.common.config.RabbitMQConfig.INTERACTION_EXCHANGE;

@Service
@AllArgsConstructor
@Slf4j
public class PostDeletedPublisher {
    private final OutboxService outboxService;

    public void publishPostDeleted(PostDeletedEvent event){
        log.info("Publishing post deleted event to outbox {}: {}", INTERACTION_EXCHANGE, event);
        outboxService.saveToOutbox(INTERACTION_EXCHANGE, "post.deleted", event);
    }
}
