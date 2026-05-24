package com.nguyenthanhbang.Social_media.dto.response;

import com.nguyenthanhbang.Social_media.common.dto.BaseResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostShareResponse extends BaseResponse {
    private String shareContent;
    private Long userId;
    private Long postId;
}

