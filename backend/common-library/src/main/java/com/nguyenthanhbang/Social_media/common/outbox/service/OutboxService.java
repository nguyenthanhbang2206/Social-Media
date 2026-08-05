package com.nguyenthanhbang.Social_media.common.outbox.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nguyenthanhbang.Social_media.common.outbox.model.OutboxMessage;
import com.nguyenthanhbang.Social_media.common.outbox.model.OutboxStatus;
import com.nguyenthanhbang.Social_media.common.outbox.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void saveToOutbox(String exchange, String routingKey, Object payloadEvent) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payloadEvent);
            OutboxMessage outboxMessage = OutboxMessage.builder()
                    .exchange(exchange)
                    .routingKey(routingKey)
                    .className(payloadEvent.getClass().getName())
                    .payload(jsonPayload)
                    .status(OutboxStatus.PENDING)
                    .retryCount(0)
                    .createdAt(Instant.now())
                    .build();
            outboxRepository.save(outboxMessage);
            log.info("Saved outbox message: exchange={}, routingKey={}, class={}", 
                    exchange, routingKey, payloadEvent.getClass().getSimpleName());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize outbox payload", e);
            throw new IllegalArgumentException("Failed to serialize event payload", e);
        }
    }
}
