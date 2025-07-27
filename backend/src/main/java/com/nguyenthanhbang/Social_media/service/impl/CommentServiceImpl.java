package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.dto.request.CommentRequest;
import com.nguyenthanhbang.Social_media.model.Comment;
import com.nguyenthanhbang.Social_media.model.Post;
import com.nguyenthanhbang.Social_media.model.User;
import com.nguyenthanhbang.Social_media.repository.CommentRepository;
import com.nguyenthanhbang.Social_media.service.CommentService;
import com.nguyenthanhbang.Social_media.service.PostService;
import com.nguyenthanhbang.Social_media.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserService userService;
    @Override
    public Comment comment(Long postId, CommentRequest request) {
        Post post = postService.getPostById(postId);
        User user = userService.getUserLogin();
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(user);
        comment.setPost(post);
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getComments(Long postId) {
        Post post = postService.getPostById(postId);
        return commentRepository.findByPostId(postId);
    }

    @Override
    public Comment updateComment(Long commentId, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> new EntityNotFoundException("Comment not found"));
        comment.setContent(request.getContent());
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> new EntityNotFoundException("Comment not found"));
        commentRepository.deleteById(commentId);
    }

    @Override
    public Comment reply(Long commentParentId, CommentRequest request) {
        Comment parentComment = commentRepository.findById(commentParentId).orElseThrow(()-> new EntityNotFoundException("Comment not found"));
        User user = userService.getUserLogin();
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(user);
        comment.setPost(parentComment.getPost());
        comment.setParentComment(parentComment);
        parentComment.getReplies().add(comment);
        return commentRepository.save(comment);
    }


    @Override
    public List<Comment> getReliesOfComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> new EntityNotFoundException("Comment not found"));
        return comment.getReplies();
    }
}
