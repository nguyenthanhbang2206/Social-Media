package com.nguyenthanhbang.Social_media.dto.response;

import com.nguyenthanhbang.Social_media.common.dto.BaseResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponse extends BaseResponse{
    private String content;
    private Long parentCommentId;
    private Long userId;
    private Long postId;
}
