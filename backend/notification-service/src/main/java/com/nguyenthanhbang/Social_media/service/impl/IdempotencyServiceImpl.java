package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.model.ProcessedMessage;
import com.nguyenthanhbang.Social_media.repository.ProcessedMessageRepository;
import com.nguyenthanhbang.Social_media.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyServiceImpl implements IdempotencyService {

    private final ProcessedMessageRepository processedMessageRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean isProcessed(String messageId, String consumerName) {
        return processedMessageRepository.findByMessageIdAndConsumerName(messageId, consumerName).isPresent();
    }

    @Override
    @Transactional
    public void markAsProcessed(String messageId, String consumerName) {
        try {
            ProcessedMessage pm = ProcessedMessage.builder()
                    .messageId(messageId)
                    .consumerName(consumerName)
                    .processedAt(Instant.now())
                    .build();
            processedMessageRepository.saveAndFlush(pm);
            log.info("Message {} successfully marked as processed by consumer {}", messageId, consumerName);
        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate message detected: Message {} has already been marked as processed by {}", messageId, consumerName);
            throw new IllegalStateException("Duplicate message detected: " + messageId, e);
        }
    }
}
