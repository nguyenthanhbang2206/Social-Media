package com.nguyenthanhbang.Social_media.service;

import com.nguyenthanhbang.Social_media.dto.request.CommentRequest;
import com.nguyenthanhbang.Social_media.dto.response.CommentResponse;
import com.nguyenthanhbang.Social_media.model.Comment;

import java.util.List;

public interface CommentService {
    Comment comment(Long postId, CommentRequest request);
    List<Comment> getComments(Long postId);
    Comment updateComment(Long commentId, CommentRequest request);
    void deleteComment(Long commentId);
    Comment reply(Long commentParentId, CommentRequest request);
    List<Comment> getReliesOfComment(Long commentId);
}
