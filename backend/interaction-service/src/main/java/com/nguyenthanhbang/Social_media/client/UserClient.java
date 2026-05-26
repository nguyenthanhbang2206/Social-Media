package com.nguyenthanhbang.Social_media.client;

import com.nguyenthanhbang.Social_media.common.dto.ApiResponse;
import com.nguyenthanhbang.Social_media.common.dto.UserSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/api/v1/internal/users/{id}")
    ApiResponse<UserSummaryResponse> getUserById(@PathVariable("id") Long id);
    @GetMapping("/api/v1/internal/blocks/exists")
    ApiResponse<Boolean> existsBlock(@RequestParam("userId") Long userId, @RequestParam("targetId") Long targetId);
}
