package com.nguyenthanhbang.Social_media.client;

import com.nguyenthanhbang.Social_media.common.dto.ApiResponse;
import com.nguyenthanhbang.Social_media.common.dto.GroupSummaryResponse;
import com.nguyenthanhbang.Social_media.common.enumeration.GroupMembershipStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "group-service")
public interface GroupClient {
    @GetMapping("/api/v1/groups/{id}")
    ApiResponse<GroupSummaryResponse> getGroupById(@PathVariable Long id);

    @GetMapping("/api/v1/groups/{groupId}/members/status")
    ApiResponse<GroupMembershipStatus> getMembershipStatus(@PathVariable Long groupId);

    @GetMapping("/api/v1/groups/{groupId}/members/me/is-admin")
    ApiResponse<Boolean> isAdmin(@PathVariable Long groupId);
}
