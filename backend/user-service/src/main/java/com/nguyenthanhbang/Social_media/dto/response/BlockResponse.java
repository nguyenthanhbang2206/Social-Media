package com.nguyenthanhbang.Social_media.dto.response;

import com.nguyenthanhbang.Social_media.common.dto.BaseResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockResponse extends BaseResponse {
    private String reason;
    private Long blockerId;
    private Long blockedId;
}

