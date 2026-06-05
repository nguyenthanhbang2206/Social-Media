package com.nguyenthanhbang.Social_media.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.nguyenthanhbang.Social_media.common.config.RabbitMQConfig.GROUP_EXCHANGE;

@Configuration
public class RabbitMQConfig {
    @Bean
    public Exchange groupExchange(){
        return new TopicExchange(GROUP_EXCHANGE);
    }
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
