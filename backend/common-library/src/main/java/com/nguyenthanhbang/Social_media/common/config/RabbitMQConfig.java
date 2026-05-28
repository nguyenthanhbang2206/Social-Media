package com.nguyenthanhbang.Social_media.common.config;


import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;

public class RabbitMQConfig {
//    exchange
    public static final String INTERACTION_EXCHANGE = "interaction.exchange";
    public static final String USER_EXCHANGE = "user.exchange";


//    queue
    public static final String POST_NOTIFICATION_QUEUE = "notification.post.queue";
    public static final String COMMENT_NOTIFICATION_QUEUE = "notification.comment.queue";

    public static final String FRIEND_NOTIFICATION_QUEUE = "notification.friend.queue";

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
