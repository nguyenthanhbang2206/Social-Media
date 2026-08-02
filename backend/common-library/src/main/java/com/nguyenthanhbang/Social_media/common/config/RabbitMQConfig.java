package com.nguyenthanhbang.Social_media.common.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("commonRabbitMQConfig")
public class RabbitMQConfig {
//    exchange
    public static final String INTERACTION_EXCHANGE = "interaction.exchange";
    public static final String USER_EXCHANGE = "user.exchange";
    public static final String GROUP_EXCHANGE = "group.exchange";


//    queue
    public static final String POST_NOTIFICATION_QUEUE = "notification.post.queue";
    public static final String COMMENT_NOTIFICATION_QUEUE = "notification.comment.queue";

    public static final String FRIEND_NOTIFICATION_QUEUE = "notification.friend.queue";
    public static final String GROUP_NOTIFICATION_QUEUE = "notification.group.queue";

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    // exchange
    @Bean
    public TopicExchange interactionExchange() {
        return new TopicExchange(INTERACTION_EXCHANGE);
    }

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    @Bean
    public TopicExchange groupExchange() {
        return new TopicExchange(GROUP_EXCHANGE);
    }


}
