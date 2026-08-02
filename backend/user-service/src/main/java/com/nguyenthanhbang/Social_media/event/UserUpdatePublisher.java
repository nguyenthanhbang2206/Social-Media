package com.nguyenthanhbang.Social_media.event;

import com.nguyenthanhbang.Social_media.common.event.UserUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.nguyenthanhbang.Social_media.common.config.RabbitMQConfig.USER_EXCHANGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserUpdatePublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishUserUpdatedEvent(UserUpdateEvent event){
        log.info("Publishing user updated event to {}: {}", USER_EXCHANGE, event);
        rabbitTemplate.convertAndSend(USER_EXCHANGE, "user.updated", event);
    }

}
