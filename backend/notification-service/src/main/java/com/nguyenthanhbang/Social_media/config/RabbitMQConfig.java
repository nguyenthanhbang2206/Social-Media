package com.nguyenthanhbang.Social_media.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.nguyenthanhbang.Social_media.common.config.RabbitMQConfig.*;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(INTERACTION_EXCHANGE);
    }

    @Bean
    public Queue postNotificationQueue(){
        return new Queue(POST_NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Queue commentNotificationQueue(){
        return new Queue(COMMENT_NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Queue friendNotificationQueue(){
        return new Queue(FRIEND_NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Binding bindingPost(
            @Qualifier("topicExchange") TopicExchange topicExchange,
            @Qualifier("postNotificationQueue") Queue postNotificationQueue
    ){
        return BindingBuilder.bind(postNotificationQueue).to(topicExchange).with("post.*");
    }

    @Bean
    public Binding bindingComment(
            @Qualifier("topicExchange") TopicExchange topicExchange,
            @Qualifier("commentNotificationQueue") Queue commentNotificationQueue
    ){
        return BindingBuilder.bind(commentNotificationQueue).to(topicExchange).with("comment.*");
    }

    @Bean
    public Binding bindingFriend(
            @Qualifier("topicExchange") TopicExchange topicExchange,
            @Qualifier("friendNotificationQueue") Queue friendNotificationQueue
    ){
        return BindingBuilder.bind(friendNotificationQueue).to(topicExchange).with("friend.*");
    }
}