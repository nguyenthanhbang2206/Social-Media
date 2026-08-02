package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.client.PostClient;
import com.nguyenthanhbang.Social_media.client.UserClient;
import com.nguyenthanhbang.Social_media.common.dto.ApiResponse;
import com.nguyenthanhbang.Social_media.common.dto.UserSummaryResponse;
import com.nguyenthanhbang.Social_media.common.event.CommentEvent;
import com.nguyenthanhbang.Social_media.common.util.RequestHeaderUtil;
import com.nguyenthanhbang.Social_media.dto.request.CommentRequest;
import com.nguyenthanhbang.Social_media.common.dto.PostSummaryResponse;
import com.nguyenthanhbang.Social_media.event.CommentPublisher;
import com.nguyenthanhbang.Social_media.model.Comment;
import com.nguyenthanhbang.Social_media.repository.CommentRepository;
import com.nguyenthanhbang.Social_media.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostClient postClient;
    private final UserClient userClient;
    private final CommentPublisher commentPublisher;

    @Override
    public Comment comment(Long postId, CommentRequest request) {
        Long userId = getCurrentUserId();
        UserSummaryResponse userSummaryResponse = userClient.getUserById(userId).getData();

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
        comment =  commentRepository.save(comment);


//        event
        String commentPreview = "";
        if(comment.getContent().length() < 50){
            commentPreview = comment.getContent();
        }else{
            commentPreview = comment.getContent().substring(0,50) + "...";
        }

        CommentEvent event = CommentEvent.builder()
                .actorId(userId)
                .actorName(userSummaryResponse.getFullName())
                .commentPreview(commentPreview)
                .reply(false)
                .postId(postId)
                .action("CREATED")
                .recipientId(post.getUserId())
                .build();

        log.info("---------------comment created event---------------");
        commentPublisher.publish(event);
        return comment;
    }

    @Override
    public List<Comment> getComments(Long postId) {
        postClient.getPostById(postId);
        return commentRepository.findByPostId(postId);
    }

    @Override
    public Comment updateComment(Long commentId, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> new EntityNotFoundException("Comment not found"));
        Long userId = getCurrentUserId();
        if (!comment.getUserId().equals(userId)) {
            throw new EntityNotFoundException("You do not have permission to update this comment");
        }
        comment.setContent(request.getContent());
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> new EntityNotFoundException("Comment not found"));
        Long userId = getCurrentUserId();
        if (!comment.getUserId().equals(userId)) {
            throw new EntityNotFoundException("You do not have permission to delete this comment");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public Comment reply(Long commentParentId, CommentRequest request) {
        Comment parentComment = commentRepository.findById(commentParentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        Long userId = getCurrentUserId();
        UserSummaryResponse userSummaryResponse = userClient.getUserById(userId).getData();

        boolean blocked = Boolean.TRUE.equals(userClient.existsBlock(userId, parentComment.getUserId()).getData());
        if (blocked) {
            throw new EntityNotFoundException("Blocked user");
        }
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUserId(userId);
        comment.setPostId(parentComment.getPostId());
        comment.setParentCommentId(parentComment.getId());
        comment = commentRepository.save(comment);

        String commentPreview = "";
        if(comment.getContent().length() < 50){
            commentPreview = comment.getContent();
        }else{
            commentPreview = comment.getContent().substring(0,50) + "...";
        }


        CommentEvent event = CommentEvent.builder()
                .actorId(userId)
                .actorName(userSummaryResponse.getFullName())
                .commentPreview(commentPreview)
                .reply(true)
                .action("CREATED")
                .postId(parentComment.getPostId())
                .parentCommentId(commentParentId)
                .recipientId(parentComment.getUserId())
                .build();

        log.info("---------------comment reply event---------------");
        commentPublisher.publish(event);
        return comment;
    }
    @Override
    public List<Comment> getReliesOfComment(Long commentId) {
        commentRepository.findById(commentId).orElseThrow(()-> new EntityNotFoundException("Comment not found"));
        return commentRepository.findByParentCommentId(commentId);
    }

    private Long getCurrentUserId() {
        String email = RequestHeaderUtil.getUserEmail()
                .orElseThrow(() -> new EntityNotFoundException("User not found - X-User-Email header missing"));
        
        ApiResponse<UserSummaryResponse> response = userClient.getUserByEmail(email);
        if (response == null || response.getData() == null) {
            throw new EntityNotFoundException("User not found with email: " + email);
        }
        
        return response.getData().getId();
    }
    @Override
    @Transactional
    public void deleteCommentsByPostId(Long postId) {
        log.info("Deleting comments for postId={}", postId);
        commentRepository.deleteByPostId(postId);
    }
}
