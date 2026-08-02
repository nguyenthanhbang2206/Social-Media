package com.nguyenthanhbang.Social_media.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
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

}
