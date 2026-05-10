package com.nguyenthanhbang.Social_media.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostShareResponse extends BaseResponse {
    private String shareContent;
    private UserResponse user;
    private Long postId;
}

