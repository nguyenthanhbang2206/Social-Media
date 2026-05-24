package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.common.util.RequestHeaderUtil;
import com.nguyenthanhbang.Social_media.dto.request.CommentLikeRequest;
import com.nguyenthanhbang.Social_media.model.Comment;
import com.nguyenthanhbang.Social_media.model.CommentLike;
import com.nguyenthanhbang.Social_media.repository.CommentLikeRepository;
import com.nguyenthanhbang.Social_media.repository.CommentRepository;
import com.nguyenthanhbang.Social_media.service.CommentLikeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentLikeServiceImpl implements CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;

    @Override
    public CommentLike reactComment(CommentLikeRequest request, Long commentId) {
        Long userId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        Optional<CommentLike> existing = commentLikeRepository.findByUserIdAndCommentId(userId, commentId);
        CommentLike commentLike = existing.orElseGet(CommentLike::new);
        commentLike.setUserId(userId);
        commentLike.setCommentId(commentId);
        commentLike.setReactionType(request.getReactionType());
        return commentLikeRepository.save(commentLike);
    }

    @Override
    public void deleteReactComment(Long commentId) {
        Long userId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        CommentLike commentLike = commentLikeRepository.findByUserIdAndCommentId(userId, commentId)
                .orElseThrow(() -> new EntityNotFoundException("Reaction not found"));
        commentLikeRepository.delete(commentLike);
    }

    @Override
    public CommentLike getReactByUserIdAndCommentId(Long commentId) {
        Long userId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        return commentLikeRepository.findByUserIdAndCommentId(userId, commentId).orElse(null);
    }

    @Override
    public List<CommentLike> getReactByComment(Long commentId) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        return commentLikeRepository.findByCommentId(commentId);
    }
}

