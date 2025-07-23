package com.nguyenthanhbang.Social_media.dto.response;

import com.nguyenthanhbang.Social_media.enumeration.ReactionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostLikeResponse extends BaseResponse{
    private ReactionType reactionType;
    private String username;
}
