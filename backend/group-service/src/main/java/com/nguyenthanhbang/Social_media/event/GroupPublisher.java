package com.nguyenthanhbang.Social_media.event;

import com.nguyenthanhbang.Social_media.common.event.GroupEvent;
import com.nguyenthanhbang.Social_media.common.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.nguyenthanhbang.Social_media.common.config.RabbitMQConfig.GROUP_EXCHANGE;

@Service
@RequiredArgsConstructor
public class GroupPublisher {

    private final OutboxService outboxService;

    public void publishGroupEvent(GroupEvent groupEvent, String routingKey){
        outboxService.saveToOutbox(GROUP_EXCHANGE, routingKey, groupEvent);
    }
}
