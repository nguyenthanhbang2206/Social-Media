package com.nguyenthanhbang.Social_media.event;

import com.nguyenthanhbang.Social_media.common.event.PostReactedEvent;
import com.nguyenthanhbang.Social_media.common.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.nguyenthanhbang.Social_media.common.config.RabbitMQConfig.INTERACTION_EXCHANGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostReactedPublisher {
    private final OutboxService outboxService;
    public void publish(PostReactedEvent event){
        outboxService.saveToOutbox(INTERACTION_EXCHANGE, "post.reacted", event);
        log.info("---------------saved to outbox: {}", event.toString());
    }
}
