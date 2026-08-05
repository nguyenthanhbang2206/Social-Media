package com.nguyenthanhbang.Social_media.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.nguyenthanhbang.Social_media.common.config.RabbitMQConfig.*;

@Configuration("notificationRabbitMQConfig")
public class RabbitMQConfig {

    // Comment Retry and DLQ configuration
    public static final String COMMENT_NOTIFICATION_RETRY_QUEUE = "notification.comment.retry";
    public static final String COMMENT_NOTIFICATION_DLQ = "notification.comment.dlq";
    public static final String POST_REACT_NOTIFICATION_RETRY_QUEUE = "notification.post.retry";
    public static final String POST_REACT_NOTIFICATION_DLQ = "notification.post.dlq";
    public static final String FRIEND_NOTIFICATION_RETRY_QUEUE = "notification.friend.retry";
    public static final String FRIEND_NOTIFICATION_DLQ = "notification.friend.dlq";
    public static final String GROUP_NOTIFICATION_RETRY_QUEUE = "notification.group.retry";
    public static final String GROUP_NOTIFICATION_DLQ = "notification.group.dlq";

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
        return BindingBuilder.bind(postNotificationQueue).to(interactionExchange).with("post.reacted");
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





//    retry
    @Bean
    public Queue commentNotificationRetryQueue(){
        return org.springframework.amqp.core.QueueBuilder.durable(COMMENT_NOTIFICATION_RETRY_QUEUE)
                .deadLetterExchange(INTERACTION_EXCHANGE)
                .ttl(5000)
                .build();
    }

    @Bean
    public Binding bindingCommentRetry(
            @Qualifier("retryExchange") DirectExchange retryExchange,
            @Qualifier("commentNotificationRetryQueue") Queue commentNotificationRetryQueue
    ){
        return BindingBuilder.bind(commentNotificationRetryQueue).to(retryExchange).with("comment.retry");
    }


    @Bean
    public Queue commentNotificationDlq(){
        return new Queue(COMMENT_NOTIFICATION_DLQ, true);
    }

    @Bean
    public Binding bindingCommentDlq(
            @Qualifier("dlxExchange") org.springframework.amqp.core.DirectExchange dlxExchange,
            @Qualifier("commentNotificationDlq") Queue commentNotificationDlq
    ){
        return BindingBuilder.bind(commentNotificationDlq).to(dlxExchange).with("comment.failed");
    }


    @Bean
    public Queue postReactRetryQueue(){
        return QueueBuilder.durable(POST_REACT_NOTIFICATION_RETRY_QUEUE)
                .ttl(5000)
                .deadLetterExchange(INTERACTION_EXCHANGE)
                .deadLetterRoutingKey("post.reacted")
                .build();
    }
    @Bean
    public Binding postReactBindingRetry(
            @Qualifier("postReactRetryQueue") Queue postReactRetryQueue,
            @Qualifier("retryExchange") DirectExchange retryExchange
    ){
        return BindingBuilder.bind(postReactRetryQueue).to(retryExchange).with("post-reacted.retry");
    }
    @Bean
    public Queue postReactDlq(){
        return new Queue(POST_REACT_NOTIFICATION_DLQ, true);
    }
    @Bean
    public Binding bindingPostReactDlq(@Qualifier("dlxExchange") DirectExchange dlxExchange, @Qualifier("postReactDlq") Queue postReactDlq){
        return BindingBuilder.bind(postReactDlq).to(dlxExchange).with("post-reacted.failed");
    }

    @Bean
    public Queue friendRetryQueue(){
        return QueueBuilder.durable(FRIEND_NOTIFICATION_RETRY_QUEUE)
                .ttl(5000)
                .deadLetterExchange(USER_EXCHANGE)
                .build();
    }
    @Bean
    public Binding bindFriendRetry(@Qualifier("friendRetryQueue") Queue friendRetryQueue,
                                   @Qualifier("retryExchange") DirectExchange retryExchange){
        return BindingBuilder.bind(friendRetryQueue).to(retryExchange).with("friend.retry");
    }

    @Bean
    public Queue friendDlq(){
        return new Queue(FRIEND_NOTIFICATION_DLQ, true);
    }
    @Bean
    public Binding bindingFriendQueue(@Qualifier("friendDlq") Queue friendDlq,
                                      @Qualifier("dlxExchange") DirectExchange dlxExchange){
        return BindingBuilder.bind(friendDlq).to(dlxExchange).with("friend.failed");
    }

    @Bean
    public Queue groupRetryQueue(){
        return QueueBuilder.durable(GROUP_NOTIFICATION_RETRY_QUEUE)
                .ttl(5000)
                .deadLetterExchange(GROUP_EXCHANGE)
                .deadLetterRoutingKey("group.retry")
                .build();
    }
    @Bean
    public Binding groupBindingRetry(
            @Qualifier("groupRetryQueue") Queue groupRetryQueue,
            @Qualifier("retryExchange") DirectExchange retryExchange
    ){
        return BindingBuilder.bind(groupRetryQueue).to(retryExchange).with("group.retry");
    }
    @Bean
    public Queue groupDlq(){
        return new Queue(GROUP_NOTIFICATION_DLQ, true);
    }
    @Bean
    public Binding groupBindingDlq(
            @Qualifier("groupDlq") Queue groupDlq,
            @Qualifier("dlxExchange") DirectExchange dlxExchange
    ){
        return BindingBuilder.bind(groupDlq).to(dlxExchange).with("group.failed");
    }
}