package com.nguyenthanhbang.Social_media.event;

import com.nguyenthanhbang.Social_media.common.event.PostReactedEvent;
import com.nguyenthanhbang.Social_media.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.nguyenthanhbang.Social_media.common.config.RabbitMQConfig.INTERACTION_EXCHANGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostReactedPublisher {
    private final RabbitTemplate rabbitTemplate;
    public void publish(PostReactedEvent event){
        rabbitTemplate.convertAndSend(INTERACTION_EXCHANGE, "post.reacted", event);
        log.info("---------------publish event: {}", event.toString());
    }
}
