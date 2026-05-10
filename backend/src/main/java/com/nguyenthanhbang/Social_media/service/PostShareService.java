package com.nguyenthanhbang.Social_media.service;

import com.nguyenthanhbang.Social_media.dto.request.PostShareRequest;
import com.nguyenthanhbang.Social_media.model.PostShare;

import java.util.List;

public interface PostShareService {
    PostShare sharePost(Long postId, PostShareRequest request);
    List<PostShare> getSharesByPost(Long postId);
    void deleteShare(Long postId, Long shareId);
}

