package com.nguyenthanhbang.Social_media.event;

import com.nguyenthanhbang.Social_media.common.event.UserUpdateEvent;
import com.nguyenthanhbang.Social_media.common.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.nguyenthanhbang.Social_media.common.config.RabbitMQConfig.USER_EXCHANGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserUpdatePublisher {
    private final OutboxService outboxService;

    public void publishUserUpdatedEvent(UserUpdateEvent event){
        log.info("Publishing user updated event to outbox {}: {}", USER_EXCHANGE, event);
        outboxService.saveToOutbox(USER_EXCHANGE, "user.updated", event);
    }
}
