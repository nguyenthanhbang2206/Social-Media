package com.nguyenthanhbang.Social_media.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockResponse extends BaseResponse {
    private String reason;
    private UserResponse blocker;
    private UserResponse blocked;
}

