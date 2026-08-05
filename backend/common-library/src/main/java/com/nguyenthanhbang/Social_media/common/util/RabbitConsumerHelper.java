package com.nguyenthanhbang.Social_media.common.util;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RabbitConsumerHelper {

    private final RabbitTemplate rabbitTemplate;

    public RabbitConsumerHelper(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public boolean handleProcessing(
            Message message,
            Channel channel,
            String retryExchange,
            String retryRoutingKey,
            String dlxExchange,
            String dlxRoutingKey,
            int maxRetries,
            MessageProcessor processor
    ) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            processor.process();
            // Success -> Ack
            channel.basicAck(deliveryTag, false);
            log.info("Successfully processed and ACKed message.");
            return true;
        } catch (Exception e) {
            log.error("Exception processing message: {}", e.getMessage(), e);
            handleFailure(message, channel, retryExchange, retryRoutingKey, dlxExchange, dlxRoutingKey, maxRetries);
            return false;
        }
    }

    private void handleFailure(
            Message message,
            Channel channel,
            String retryExchange,
            String retryRoutingKey,
            String dlxExchange,
            String dlxRoutingKey,
            int maxRetries
    ) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        int retryCount = getRetryCount(message);

        log.warn("Message processing failed. Current retry count parsed from x-death: {}", retryCount);

        if (retryCount >= maxRetries) {
            log.error("Max retries ({}) exceeded. Routing to DLQ (exchange: {}, routingKey: {})",
                    maxRetries, dlxExchange, dlxRoutingKey);
            // Send to Dead Letter Queue (DLQ)
            rabbitTemplate.send(dlxExchange, dlxRoutingKey, message);
            channel.basicAck(deliveryTag, false);
        } else {
            int nextRetryAttempt = retryCount + 1;
            log.info("Routing message to retry queue (attempt {}/{}) (exchange: {}, routingKey: {})",
                    nextRetryAttempt, maxRetries, retryExchange, retryRoutingKey);
            // Send to retry queue
            rabbitTemplate.send(retryExchange, retryRoutingKey, message);
            channel.basicAck(deliveryTag, false);
        }
    }

    public int getRetryCount(Message message) {
        Object xDeathObj = message.getMessageProperties().getHeaders().get("x-death");
        if (xDeathObj instanceof List) {
            List<?> xDeathList = (List<?>) xDeathObj;
            long totalCount = 0;
            log.info("--- Inspecting RabbitMQ x-death Header ---");
            for (Object obj : xDeathList) {
                if (obj instanceof Map) {
                    Map<?, ?> entry = (Map<?, ?>) obj;
                    log.info("x-death entry - Queue: {}, Reason: {}, Count: {}, Exchange: {}, Time: {}",
                            entry.get("queue"), entry.get("reason"), entry.get("count"), entry.get("exchange"), entry.get("time"));
                    
                    Object queue = entry.get("queue");
                    if (queue != null && queue.toString().contains("retry")) {
                        Object count = entry.get("count");
                        if (count instanceof Number) {
                            totalCount += ((Number) count).longValue();
                        }
                    }
                }
            }
            log.info("Total retry count from x-death: {}", totalCount);
            return (int) totalCount;
        }
        return 0;
    }

    @FunctionalInterface
    public interface MessageProcessor {
        void process() throws Exception;
    }
}
