package com.nguyenthanhbang.Social_media.event;

import com.nguyenthanhbang.Social_media.common.event.FriendEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.nguyenthanhbang.Social_media.common.config.RabbitMQConfig.USER_EXCHANGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendRequestPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishFriendRequest(FriendEvent event){
        rabbitTemplate.convertAndSend(USER_EXCHANGE, "friend.request", event);
    }

}
