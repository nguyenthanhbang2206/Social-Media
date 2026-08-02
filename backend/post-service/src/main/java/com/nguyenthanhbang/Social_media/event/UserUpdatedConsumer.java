package com.nguyenthanhbang.Social_media.event;

import com.nguyenthanhbang.Social_media.common.event.UserUpdateEvent;
import com.nguyenthanhbang.Social_media.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static com.nguyenthanhbang.Social_media.config.RabbitMQConfig.USER_UPDATED_QUEUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserUpdatedConsumer {

    private final PostRepository postRepository;
    @RabbitListener(queues = USER_UPDATED_QUEUE)
    public void consumeUserUpdated(UserUpdateEvent event){
        if (event == null || event.getUserId() == null) {
            log.warn("Received empty or invalid UserUpdatedEvent");
            return;
        }
        log.info("Received UserUpdatedEvent: {}", event);
        try {
            postRepository.updateUserPosts(event.getUserId(), event.getFullName(), event.getActive());
            log.info("Successfully updated posts for userId={} with fullName={} and active={}",
                    event.getUserId(), event.getFullName(), event.getActive());
        }catch (Exception e){
            log.error("Failed to update posts for userId={}", event.getUserId(), e);
        }
    }
}
