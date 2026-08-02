package com.nguyenthanhbang.Social_media.client;

import com.nguyenthanhbang.Social_media.common.dto.ApiResponse;
import com.nguyenthanhbang.Social_media.common.dto.UserSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client gọi sang user-service.
 * Đặt tại: interaction-service/src/main/java/com/nguyenthanhbang/Social_media/client/UserClient.java
 *
 * Dùng bởi: CommentServiceImpl, PostLikeServiceImpl, CommentLikeServiceImpl
 *   - getUserById: lấy thông tin user khi tạo comment/like
 *   - getUserByEmail: getCurrentUserId() resolve Long id từ email header
 *   - existsBlock: kiểm tra block giữa 2 user trước khi cho phép tương tác
 */
@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/v1/users/{id}")
    ApiResponse<UserSummaryResponse> getUserById(@PathVariable("id") Long id);

    @GetMapping("/api/v1/users/email/{email}")
    ApiResponse<UserSummaryResponse> getUserByEmail(@PathVariable("email") String email);

    @GetMapping("/api/v1/blocks/exists")
    ApiResponse<Boolean> existsBlock(@RequestParam("userId") Long userId,
                                     @RequestParam("targetId") Long targetId);
}
