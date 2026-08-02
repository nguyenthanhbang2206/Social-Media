package com.nguyenthanhbang.Social_media.event;

import com.nguyenthanhbang.Social_media.common.event.PostDeletedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import static com.nguyenthanhbang.Social_media.common.config.RabbitMQConfig.INTERACTION_EXCHANGE;

@Service
@AllArgsConstructor
@Slf4j
public class PostDeletedPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishPostDeleted(PostDeletedEvent event){
        log.info("Publishing post deleted event to {}: {}", INTERACTION_EXCHANGE, event);
        rabbitTemplate.convertAndSend(INTERACTION_EXCHANGE, "post.deleted", event);
    }
}
