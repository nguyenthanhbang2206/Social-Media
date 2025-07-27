package com.nguyenthanhbang.Social_media.controller.user;

import com.nguyenthanhbang.Social_media.dto.request.CommentRequest;
import com.nguyenthanhbang.Social_media.dto.response.ApiResponse;
import com.nguyenthanhbang.Social_media.dto.response.CommentResponse;
import com.nguyenthanhbang.Social_media.dto.response.PostResponse;
import com.nguyenthanhbang.Social_media.mapper.CommentMapper;
import com.nguyenthanhbang.Social_media.model.Comment;
import com.nguyenthanhbang.Social_media.model.Post;
import com.nguyenthanhbang.Social_media.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class CommentController {
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> comment(@PathVariable Long postId, @RequestBody CommentRequest commentRequest) {
        Comment comment = commentService.comment(postId, commentRequest);
        ApiResponse response = ApiResponse.builder()
                .message("Comment successfully")
                .status(HttpStatus.CREATED.value())
                .data(commentMapper.toCommentResponse(comment))
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(@PathVariable Long postId) {
        List<Comment> comments = commentService.getComments(postId);
        ApiResponse response = ApiResponse.builder()
                .message("Get comments successfully")
                .status(HttpStatus.OK.value())
                .data(commentMapper.toCommentResponses(comments))
                .build();
        return ResponseEntity.ok(response);
    }
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(@PathVariable Long commentId, @RequestBody CommentRequest commentRequest) {
        Comment comment = commentService.updateComment(commentId, commentRequest);
        ApiResponse response = ApiResponse.builder()
                .message("Update comment successfully")
                .status(HttpStatus.OK.value())
                .data(commentMapper.toCommentResponse(comment))
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        ApiResponse response = ApiResponse.builder()
                .message("Delete comment successfully")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
    @PostMapping("/comments/{commentId}/reply")
    public ResponseEntity<ApiResponse<CommentResponse>> replyComment(@PathVariable Long commentId, @RequestBody CommentRequest commentRequest) {
        Comment comment = commentService.reply(commentId, commentRequest);
        ApiResponse response = ApiResponse.builder()
                .message("Reply comment successfully")
                .status(HttpStatus.CREATED.value())
                .data(commentMapper.toCommentResponse(comment))
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getReliesOfComment(@PathVariable Long commentId) {
        List<Comment> comments = commentService.getReliesOfComment(commentId);
        ApiResponse response = ApiResponse.builder()
                .message("Get replies comment successfully")
                .status(HttpStatus.OK.value())
                .data(commentMapper.toCommentResponses(comments))
                .build();
        return ResponseEntity.ok(response);
    }
}
