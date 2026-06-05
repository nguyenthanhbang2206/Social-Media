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
    public TopicExchange interactionExchange(){
        return new TopicExchange(INTERACTION_EXCHANGE);
    }
    @Bean
    public TopicExchange userExchange(){
        return new TopicExchange(USER_EXCHANGE);
    }
    @Bean
    public TopicExchange groupExchange(){
        return new TopicExchange(GROUP_EXCHANGE);
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
    public Queue groupNotificationQueue(){
        return new Queue(GROUP_NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Binding bindingPost(
            @Qualifier("interactionExchange") TopicExchange interactionExchange,
            @Qualifier("postNotificationQueue") Queue postNotificationQueue
    ){
        return BindingBuilder.bind(postNotificationQueue).to(interactionExchange).with("post.*");
    }

    @Bean
    public Binding bindingComment(
            @Qualifier("interactionExchange") TopicExchange interactionExchange,
            @Qualifier("commentNotificationQueue") Queue commentNotificationQueue
    ){
        return BindingBuilder.bind(commentNotificationQueue).to(interactionExchange).with("comment.*");
    }

    @Bean
    public Binding bindingFriend(
            @Qualifier("userExchange") TopicExchange userExchange,
            @Qualifier("friendNotificationQueue") Queue friendNotificationQueue
    ){
        return BindingBuilder.bind(friendNotificationQueue).to(userExchange).with("friend.*");
    }
    @Bean
    public Binding bindGroup(@Qualifier("groupExchange") TopicExchange groupExchange,
                             @Qualifier("groupNotificationQueue") Queue groupNotificationQueue){
        return BindingBuilder.bind(groupNotificationQueue).to(groupExchange).with("group.*");
    }
}