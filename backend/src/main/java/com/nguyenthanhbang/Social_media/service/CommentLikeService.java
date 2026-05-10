package com.nguyenthanhbang.Social_media.service;

import com.nguyenthanhbang.Social_media.dto.request.CommentLikeRequest;
import com.nguyenthanhbang.Social_media.model.CommentLike;

import java.util.List;

public interface CommentLikeService {
    CommentLike reactComment(CommentLikeRequest request, Long commentId);
    void deleteReactComment(Long commentId);
    CommentLike getReactByUserIdAndCommentId(Long commentId);
    List<CommentLike> getReactByComment(Long commentId);
}

