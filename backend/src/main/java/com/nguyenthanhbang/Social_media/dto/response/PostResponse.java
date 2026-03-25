package com.nguyenthanhbang.Social_media.dto.response;

import com.nguyenthanhbang.Social_media.enumeration.PostType;
import com.nguyenthanhbang.Social_media.enumeration.PrivacyLevel;
import com.nguyenthanhbang.Social_media.model.*;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostResponse extends BaseResponse {
    private String content;
    private PrivacyLevel privacy;
    private Boolean isApproved;
    private Boolean isPinned;
    private PostType postType;
    private List<PostMediaResponse> media;
    private Long totalReactions;
    private Long totalComments;
    private Long totalShares;
}
