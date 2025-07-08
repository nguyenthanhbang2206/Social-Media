package com.nguyenthanhbang.Social_media.dto.request;

import com.nguyenthanhbang.Social_media.enumeration.PrivacyLevel;
import com.nguyenthanhbang.Social_media.model.PostMedia;
import lombok.Getter;

import java.util.List;

@Getter
public class CreatePostRequest {
    private String content;
    private PrivacyLevel privacy;
    private List<PostMediaRequest> media;
}
