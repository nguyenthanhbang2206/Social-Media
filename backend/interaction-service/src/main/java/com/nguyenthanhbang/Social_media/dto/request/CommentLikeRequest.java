package com.nguyenthanhbang.Social_media.dto.request;

import com.nguyenthanhbang.Social_media.common.enumeration.ReactionType;
import lombok.Getter;

@Getter
public class CommentLikeRequest {
    private ReactionType reactionType;
}

