package com.nguyenthanhbang.Social_media.service;

import com.nguyenthanhbang.Social_media.dto.request.PostLikeRequest;
import com.nguyenthanhbang.Social_media.model.PostLike;

public interface PostLikeService {
    PostLike reactPost(PostLikeRequest request, Long postId);
    void deleteReactPost(Long postId);
    PostLike getReactByUserIdAndPostId(Long postId);

}
