package com.nguyenthanhbang.Social_media.dto.response;

import com.nguyenthanhbang.Social_media.common.dto.BaseResponse;
import com.nguyenthanhbang.Social_media.common.enumeration.PostType;
import com.nguyenthanhbang.Social_media.common.enumeration.PrivacyLevel;
import com.nguyenthanhbang.Social_media.common.enumeration.ReactionType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostResponse extends BaseResponse {
    private String content;
    private PrivacyLevel privacy;
    private Boolean isApproved;
    private Boolean isPinned;
    private PostType postType;
    private Long userId;
    private String ownerName;
    private Long groupId;
    private List<PostMediaResponse> media;
    private Long totalReactions;
    private Long totalComments;
    private Long totalShares;
    private ReactionType myReactionType;
}
