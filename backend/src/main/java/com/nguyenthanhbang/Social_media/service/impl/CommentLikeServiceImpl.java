package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.dto.request.CommentLikeRequest;
import com.nguyenthanhbang.Social_media.model.Comment;
import com.nguyenthanhbang.Social_media.model.CommentLike;
import com.nguyenthanhbang.Social_media.model.User;
import com.nguyenthanhbang.Social_media.repository.CommentLikeRepository;
import com.nguyenthanhbang.Social_media.repository.CommentRepository;
import com.nguyenthanhbang.Social_media.service.CommentLikeService;
import com.nguyenthanhbang.Social_media.service.UserService;
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
    private final UserService userService;

    @Override
    public CommentLike reactComment(CommentLikeRequest request, Long commentId) {
        User user = userService.getUserLogin();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        Optional<CommentLike> existing = commentLikeRepository.findByUserIdAndCommentId(user.getId(), commentId);
        CommentLike commentLike = existing.orElseGet(CommentLike::new);
        commentLike.setUser(user);
        commentLike.setComment(comment);
        commentLike.setReactionType(request.getReactionType());
        return commentLikeRepository.save(commentLike);
    }

    @Override
    public void deleteReactComment(Long commentId) {
        User user = userService.getUserLogin();
        CommentLike commentLike = commentLikeRepository.findByUserIdAndCommentId(user.getId(), commentId)
                .orElseThrow(() -> new EntityNotFoundException("Reaction not found"));
        commentLikeRepository.delete(commentLike);
    }

    @Override
    public CommentLike getReactByUserIdAndCommentId(Long commentId) {
        User user = userService.getUserLogin();
        return commentLikeRepository.findByUserIdAndCommentId(user.getId(), commentId).orElse(null);
    }

    @Override
    public List<CommentLike> getReactByComment(Long commentId) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        return commentLikeRepository.findByCommentId(commentId);
    }
}

