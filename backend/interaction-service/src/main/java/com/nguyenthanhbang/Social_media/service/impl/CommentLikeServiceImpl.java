package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.client.UserClient;
import com.nguyenthanhbang.Social_media.common.dto.UserSummaryResponse;
import com.nguyenthanhbang.Social_media.common.event.CommentEvent;
import com.nguyenthanhbang.Social_media.common.util.RequestHeaderUtil;
import com.nguyenthanhbang.Social_media.dto.request.CommentLikeRequest;
import com.nguyenthanhbang.Social_media.event.CommentPublisher;
import com.nguyenthanhbang.Social_media.model.Comment;
import com.nguyenthanhbang.Social_media.model.CommentLike;
import com.nguyenthanhbang.Social_media.repository.CommentLikeRepository;
import com.nguyenthanhbang.Social_media.repository.CommentRepository;
import com.nguyenthanhbang.Social_media.service.CommentLikeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentLikeServiceImpl implements CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final UserClient userClient;
    private final CommentPublisher commentPublisher;

    @Override
    public CommentLike reactComment(CommentLikeRequest request, Long commentId) {
        Long userId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        UserSummaryResponse userSummaryResponse = userClient.getUserById(userId).getData();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        Optional<CommentLike> existing = commentLikeRepository.findByUserIdAndCommentId(userId, commentId);
        CommentLike commentLike = existing.orElseGet(CommentLike::new);
        commentLike.setUserId(userId);
        commentLike.setCommentId(commentId);
        commentLike.setReactionType(request.getReactionType());
        commentLike = commentLikeRepository.save(commentLike);

        String commentPreview = "";
        if(comment.getContent().length() < 50){
            commentPreview = comment.getContent();
        }else{
            commentPreview = comment.getContent().substring(0,50) + "...";
        }


        CommentEvent event = CommentEvent
                .builder()
                .action("REACT")
                .actorId(userId)
                .actorName(userSummaryResponse.getFullName())
                .commentPreview(commentPreview)
                .commentId(commentId)
                .recipientId(comment.getUserId())
                .postId(comment.getPostId())
                .build();
        log.info("------publish comment like-------");
        commentPublisher.publishCommentReact(event);
        return commentLike;
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

