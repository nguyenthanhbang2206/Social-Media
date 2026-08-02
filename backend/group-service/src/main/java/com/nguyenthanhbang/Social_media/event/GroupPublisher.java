package com.nguyenthanhbang.Social_media.event;

import com.nguyenthanhbang.Social_media.common.event.GroupEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.nguyenthanhbang.Social_media.common.config.RabbitMQConfig.GROUP_EXCHANGE;

@Service
@RequiredArgsConstructor
public class GroupPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishGroupEvent(GroupEvent groupEvent, String routingKey){
        rabbitTemplate.convertAndSend(GROUP_EXCHANGE, routingKey, groupEvent);
    }
}
