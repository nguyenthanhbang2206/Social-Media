package com.nguyenthanhbang.Social_media.dto.request;

import com.nguyenthanhbang.Social_media.enumeration.ReactionType;
import lombok.Getter;

@Getter
public class PostLikeRequest {
    private ReactionType reactionType;
}
