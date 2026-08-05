package com.nguyenthanhbang.Social_media.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration("commonRabbitMQConfig")
public class RabbitMQConfig {
//    exchange
    public static final String INTERACTION_EXCHANGE = "interaction.exchange";
    public static final String USER_EXCHANGE = "user.exchange";
    public static final String GROUP_EXCHANGE = "group.exchange";
    public static final String RETRY_EXCHANGE = "retry.exchange";
    public static final String DLX_EXCHANGE = "dlx.exchange";


//    queue
    public static final String POST_NOTIFICATION_QUEUE = "notification.post.queue";
    public static final String COMMENT_NOTIFICATION_QUEUE = "notification.comment.queue";

    public static final String FRIEND_NOTIFICATION_QUEUE = "notification.friend.queue";
    public static final String GROUP_NOTIFICATION_QUEUE = "notification.group.queue";

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        template.setMandatory(true);

        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("RabbitMQ: Message successfully delivered to exchange. CorrelationData: {}", correlationData);
            } else {
                log.error("RabbitMQ: Message delivery to exchange failed. CorrelationData: {}, Cause: {}", correlationData, cause);
            }
        });

        template.setReturnsCallback(returned -> {
            log.error("RabbitMQ: Message returned. Reply Code: {}, Reply Text: {}, Exchange: {}, Routing Key: {}, Message: {}",
                    returned.getReplyCode(), returned.getReplyText(), returned.getExchange(), returned.getRoutingKey(), returned.getMessage());
        });

        return template;
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


    @Bean
    public DirectExchange retryExchange(){
        return new DirectExchange(RETRY_EXCHANGE);
    }
    @Bean
    public DirectExchange dlxExchange(){
        return new DirectExchange(DLX_EXCHANGE);
    }



}
