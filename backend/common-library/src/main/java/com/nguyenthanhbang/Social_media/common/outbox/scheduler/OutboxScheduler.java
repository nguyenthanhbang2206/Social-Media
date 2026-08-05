package com.nguyenthanhbang.Social_media.common.outbox.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nguyenthanhbang.Social_media.common.outbox.model.OutboxMessage;
import com.nguyenthanhbang.Social_media.common.outbox.model.OutboxStatus;
import com.nguyenthanhbang.Social_media.common.outbox.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "outbox.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelayString = "${outbox.scheduler.delay-ms:2000}")
    @Transactional
    public void processOutbox() {
        // Retrieve and lock up to 10 PENDING messages using SELECT FOR UPDATE SKIP LOCKED
        List<OutboxMessage> messages = outboxRepository.findMessagesForProcessing(OutboxStatus.PENDING.name(), 10);
        if (messages.isEmpty()) {
            return;
        }

        log.info("Processing {} outbox messages...", messages.size());

        for (OutboxMessage message : messages) {
            try {
                // Deserialize payload back to its original class type
                Class<?> clazz = Class.forName(message.getClassName());
                Object event = objectMapper.readValue(message.getPayload(), clazz);

                // Publish to RabbitMQ
                rabbitTemplate.convertAndSend(message.getExchange(), message.getRoutingKey(), event);

                // Update outbox message status to COMPLETED
                message.setStatus(OutboxStatus.COMPLETED);
                message.setProcessedAt(Instant.now());
                message.setErrorMessage(null);
                outboxRepository.save(message);

                log.info("Successfully published outbox message ID {} to exchange={}, routingKey={}",
                        message.getId(), message.getExchange(), message.getRoutingKey());

            } catch (Exception e) {
                log.error("Failed to process outbox message ID {}", message.getId(), e);

                int nextRetry = message.getRetryCount() + 1;
                message.setRetryCount(nextRetry);
                message.setErrorMessage(e.getMessage());

                if (nextRetry >= 5) {
                    message.setStatus(OutboxStatus.FAILED);
                } else {
                    message.setStatus(OutboxStatus.PENDING);
                }
                
                message.setProcessedAt(Instant.now());
                outboxRepository.save(message);
            }
        }
    }
}
