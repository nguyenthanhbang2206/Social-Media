package com.nguyenthanhbang.Social_media.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.nguyenthanhbang.Social_media.common.config.RabbitMQConfig.INTERACTION_EXCHANGE;

@Configuration("interactionRabbitMQConfig")
public class RabbitMQConfig {

    public static final String POST_DELETED_QUEUE = "interaction.post-deleted.queue";

    @Bean
    public Queue postDeletedQueue(){
        return new Queue(POST_DELETED_QUEUE, true);
    }

    @Bean
    public Binding bindingPostDeleted(@Qualifier("interactionExchange") TopicExchange interactionExchange,
                                      @Qualifier("postDeletedQueue") Queue postDeletedQueue){
        return BindingBuilder.bind(postDeletedQueue).to(interactionExchange).with("post.deleted");
    }

    public static final String POST_DELETED_RETRY_QUEUE = "interaction.post-deleted.retry";
    public static final String POST_DELETED_DLQ = "interaction.post-deleted.dlq";

    @Bean
    public Queue postDeletedRetryQueue(){
        return QueueBuilder.durable(POST_DELETED_RETRY_QUEUE)
                .deadLetterExchange(INTERACTION_EXCHANGE)
                .deadLetterRoutingKey("post.deleted")
                .ttl(5000)
                .build();
    }

    @Bean
    public Queue postDeletedDlq(){
        return new Queue(POST_DELETED_DLQ, true);
    }

    @Bean
    public Binding bindingPostDeletedRetry(
            @Qualifier("retryExchange") DirectExchange retryExchange,
            @Qualifier("postDeletedRetryQueue") Queue postDeletedRetryQueue
    ){
        return BindingBuilder.bind(postDeletedRetryQueue).to(retryExchange).with("post.deleted.retry");
    }

    @Bean
    public Binding bindingPostDeletedDlq(
            @Qualifier("dlxExchange") DirectExchange dlxExchange,
            @Qualifier("postDeletedDlq") Queue postDeletedDlq
    ){
        return BindingBuilder.bind(postDeletedDlq).to(dlxExchange).with("post.deleted.failed");
    }
}
