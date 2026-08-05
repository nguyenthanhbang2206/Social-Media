package com.nguyenthanhbang.Social_media.event;

import com.nguyenthanhbang.Social_media.common.event.FriendEvent;
import com.nguyenthanhbang.Social_media.common.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.nguyenthanhbang.Social_media.common.config.RabbitMQConfig.USER_EXCHANGE;
@Slf4j
@Service
@RequiredArgsConstructor
public class FriendAcceptedPublisher {
    private final OutboxService outboxService;
    public void publishFriendAccepted(FriendEvent event){
        outboxService.saveToOutbox(USER_EXCHANGE, "friend.accepted", event);
    }
}
