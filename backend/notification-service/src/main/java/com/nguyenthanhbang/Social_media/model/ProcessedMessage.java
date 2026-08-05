package com.nguyenthanhbang.Social_media.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "processed_messages")
@IdClass(ProcessedMessage.ProcessedMessageId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedMessage {

    @Id
    @Column(name = "message_id", nullable = false)
    private String messageId;

    @Id
    @Column(name = "consumer_name", nullable = false)
    private String consumerName;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessedMessageId implements Serializable {
        private String messageId;
        private String consumerName;
    }
}
