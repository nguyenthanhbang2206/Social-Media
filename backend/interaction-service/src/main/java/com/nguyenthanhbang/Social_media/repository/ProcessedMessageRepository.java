package com.nguyenthanhbang.Social_media.repository;

import com.nguyenthanhbang.Social_media.model.ProcessedMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, ProcessedMessage.ProcessedMessageId> {
    Optional<ProcessedMessage> findByMessageIdAndConsumerName(String messageId, String consumerName);
}
