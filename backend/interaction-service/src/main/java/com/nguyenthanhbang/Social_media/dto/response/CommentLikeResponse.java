package com.nguyenthanhbang.Social_media.dto.response;

import com.nguyenthanhbang.Social_media.common.dto.BaseResponse;
import com.nguyenthanhbang.Social_media.common.enumeration.ReactionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentLikeResponse extends BaseResponse{
    private ReactionType reactionType;
    private Long userId;
}
