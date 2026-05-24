package com.nguyenthanhbang.Social_media.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostSummaryResponse extends BaseResponse {
    private Long userId;
    private Long groupId;
}
