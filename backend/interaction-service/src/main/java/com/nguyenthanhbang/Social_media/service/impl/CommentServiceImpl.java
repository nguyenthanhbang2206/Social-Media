package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.client.PostClient;
import com.nguyenthanhbang.Social_media.client.UserClient;
import com.nguyenthanhbang.Social_media.common.util.RequestHeaderUtil;
import com.nguyenthanhbang.Social_media.dto.request.CommentRequest;
import com.nguyenthanhbang.Social_media.common.dto.PostSummaryResponse;
import com.nguyenthanhbang.Social_media.model.Comment;
import com.nguyenthanhbang.Social_media.repository.CommentRepository;
import com.nguyenthanhbang.Social_media.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostClient postClient;
    private final UserClient userClient;
    @Override
    public Comment comment(Long postId, CommentRequest request) {
        Long userId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        PostSummaryResponse post = postClient.getPostById(postId).getData();
        if (post == null) {
            throw new EntityNotFoundException("Post not found");
        }
        boolean blocked = Boolean.TRUE.equals(userClient.existsBlock(userId, post.getUserId()).getData());
        if (blocked) {
            throw new EntityNotFoundException("Blocked user");
        }
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUserId(userId);
        comment.setPostId(postId);
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getComments(Long postId) {
        postClient.getPostById(postId);
        return commentRepository.findByPostId(postId);
    }

    @Override
    public Comment updateComment(Long commentId, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> new EntityNotFoundException("Comment not found"));
        Long userId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!comment.getUserId().equals(userId)) {
            throw new EntityNotFoundException("You do not have permission to update this comment");
        }
        comment.setContent(request.getContent());
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> new EntityNotFoundException("Comment not found"));
        Long userId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!comment.getUserId().equals(userId)) {
            throw new EntityNotFoundException("You do not have permission to delete this comment");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public Comment reply(Long commentParentId, CommentRequest request) {
        Comment parentComment = commentRepository.findById(commentParentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        Long userId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        boolean blocked = Boolean.TRUE.equals(userClient.existsBlock(userId, parentComment.getUserId()).getData());
        if (blocked) {
            throw new EntityNotFoundException("Blocked user");
        }
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUserId(userId);
        comment.setPostId(parentComment.getPostId());
        comment.setParentCommentId(parentComment.getId());
        return commentRepository.save(comment);
    }
    @Override
    public List<Comment> getReliesOfComment(Long commentId) {
        commentRepository.findById(commentId).orElseThrow(()-> new EntityNotFoundException("Comment not found"));
        return commentRepository.findByParentCommentId(commentId);
    }
}
