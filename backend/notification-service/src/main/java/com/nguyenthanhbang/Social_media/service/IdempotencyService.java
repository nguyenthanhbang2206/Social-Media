package com.nguyenthanhbang.Social_media.service;

public interface IdempotencyService {
    boolean isProcessed(String messageId, String consumerName);
    void markAsProcessed(String messageId, String consumerName);
}
