package com.nguyenthanhbang.Social_media.client;

import com.nguyenthanhbang.Social_media.common.dto.ApiResponse;
import com.nguyenthanhbang.Social_media.common.dto.UserSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client gọi sang user-service.
 * Đặt tại: notification-service/src/main/java/com/nguyenthanhbang/Social_media/client/UserClient.java
 *
 * Dùng bởi: NotificationServiceImpl, NotificationConsumer
 *   - getUserByEmail: getCurrentUserId() resolve Long id từ email header
 *   - getUserById: enrichWithActorInfo() lấy fullName + avatar của actor
 */
@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/v1/users/{id}")
    ApiResponse<UserSummaryResponse> getUserById(@PathVariable("id") Long id);

    @GetMapping("/api/v1/users/email/{email}")
    ApiResponse<UserSummaryResponse> getUserByEmail(@PathVariable("email") String email);
}
