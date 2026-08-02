package com.nguyenthanhbang.Social_media.dto.request;


import com.nguyenthanhbang.Social_media.common.enumeration.PostType;
import com.nguyenthanhbang.Social_media.common.enumeration.PrivacyLevel;
import com.nguyenthanhbang.Social_media.model.PostMedia;
import lombok.Getter;

import java.util.List;

@Getter
public class UpdatePostRequest {
    private String content;
    private PrivacyLevel privacy;
    private PostType postType;
    private List<PostMediaRequest> media;
}
