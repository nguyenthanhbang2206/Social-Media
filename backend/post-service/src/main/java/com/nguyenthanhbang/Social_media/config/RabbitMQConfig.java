package com.nguyenthanhbang.Social_media.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("postRabbitMQConfig")
public class RabbitMQConfig {
    public static final String USER_UPDATED_QUEUE = "post.user-updated.queue";

    @Bean
    public Queue userUpdatedQueue(){
        return new Queue(USER_UPDATED_QUEUE, true);
    }
    @Bean
    public Binding bindingUserUpdated(@Qualifier("userExchange") TopicExchange userExchange,@Qualifier("userUpdatedQueue") Queue userUpdatedQueue){
        return BindingBuilder.bind(userUpdatedQueue).to(userExchange).with("user.updated");
    }


}
