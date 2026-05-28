package com.nguyenthanhbang.Social_media.client;

import com.nguyenthanhbang.Social_media.common.dto.UserSummaryResponse;
import com.nguyenthanhbang.Social_media.common.enumeration.Gender;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.concurrent.CompletedFuture;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceClient {
    private final UserClient userClient;
    int count = 1;

    @Retry(name = "userService")
    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackGetUser")
    @TimeLimiter(name = "userService") // Đã bỏ fallbackMethod ở đây để CircuitBreaker bọc ngoài quản lý
    public CompletableFuture<UserSummaryResponse> getUserById(Long id) {
        log.info("Calling user-service {}", count++);

        // Bọc lệnh gọi Feign vào bên trong CompletableFuture
        return CompletableFuture.supplyAsync(() -> userClient.getUserById(id).getData());
    }

    // Hàm Fallback cũng phải trả về CompletableFuture
    public CompletableFuture<UserSummaryResponse> fallbackGetUser(Long id, Exception ex) {
        log.error("--------------fallback get user---------- Lý do: {}", ex.getMessage());

        // Tạo một Future chứa Exception để ném ra ngoài
        CompletableFuture<UserSummaryResponse> future = new CompletableFuture<>();
        future.completeExceptionally(new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE, "User-Service hiện không khả dụng, thử lại sau!"));
        return future;
    }
}