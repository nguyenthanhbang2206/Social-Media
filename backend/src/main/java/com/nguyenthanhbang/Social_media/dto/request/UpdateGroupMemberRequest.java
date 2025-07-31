package com.nguyenthanhbang.Social_media.dto.request;

import com.nguyenthanhbang.Social_media.enumeration.GroupRole;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
public class UpdateGroupMemberRequest {
    private GroupRole role;
}
