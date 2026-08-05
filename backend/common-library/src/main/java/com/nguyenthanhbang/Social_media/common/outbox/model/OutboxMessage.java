package com.nguyenthanhbang.Social_media.common.outbox.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "outbox_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OutboxMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String exchange;

    @Column(nullable = false, name = "routing_key")
    private String routingKey;

    @Column(nullable = false, name = "class_name")
    private String className;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OutboxStatus status = OutboxStatus.PENDING;

    @Column(nullable = false, name = "retry_count")
    @Builder.Default
    private int retryCount = 0;

    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}
