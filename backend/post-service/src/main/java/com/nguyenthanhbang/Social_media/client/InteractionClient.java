package com.nguyenthanhbang.Social_media.client;

import com.nguyenthanhbang.Social_media.common.dto.ApiResponse;
import com.nguyenthanhbang.Social_media.common.dto.PostInteractionCountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "interaction-service")
public interface InteractionClient {
    @GetMapping("/api/v1/posts/{postId}/counts")
    ApiResponse<PostInteractionCountResponse> getPostCounts(@PathVariable Long postId);
}
