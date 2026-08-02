package com.nguyenthanhbang.Social_media.controller.user;

import com.nguyenthanhbang.Social_media.dto.request.CommentLikeRequest;
import com.nguyenthanhbang.Social_media.common.dto.ApiResponse;
import com.nguyenthanhbang.Social_media.dto.response.CommentLikeResponse;
import com.nguyenthanhbang.Social_media.mapper.CommentLikeMapper;
import com.nguyenthanhbang.Social_media.model.CommentLike;
import com.nguyenthanhbang.Social_media.service.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class CommentLikeController {
    private final CommentLikeService commentLikeService;
    private final CommentLikeMapper commentLikeMapper;

    @PostMapping("/comments/{commentId}/react")
    public ResponseEntity<ApiResponse<CommentLikeResponse>> reactComment(@RequestBody CommentLikeRequest request,
                                                                         @PathVariable("commentId") Long commentId) {
        CommentLike commentLike = commentLikeService.reactComment(request, commentId);
        ApiResponse response = ApiResponse.builder()
                .message("React comment successfully")
                .status(HttpStatus.CREATED.value())
                .data(commentLikeMapper.toCommentLikeResponse(commentLike))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/comments/{commentId}/reactions")
    public ResponseEntity<ApiResponse<List<CommentLikeResponse>>> getReactions(@PathVariable("commentId") Long commentId) {
        List<CommentLike> commentLikes = commentLikeService.getReactByComment(commentId);
        ApiResponse response = ApiResponse.builder()
                .message("Get react comment successfully")
                .status(HttpStatus.OK.value())
                .data(commentLikeMapper.toCommentLikeResponses(commentLikes))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/comments/{commentId}/me")
    public ResponseEntity<ApiResponse<CommentLikeResponse>> getMyReaction(@PathVariable("commentId") Long commentId) {
        CommentLike commentLike = commentLikeService.getReactByUserIdAndCommentId(commentId);
        ApiResponse response = ApiResponse.builder()
                .message("Get react comment successfully")
                .status(HttpStatus.OK.value())
                .data(commentLike == null ? null : commentLikeMapper.toCommentLikeResponse(commentLike))
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{commentId}/un-react")
    public ResponseEntity<ApiResponse<Void>> deleteReaction(@PathVariable("commentId") Long commentId) {
        commentLikeService.deleteReactComment(commentId);
        ApiResponse response = ApiResponse.builder()
                .message("Delete react comment successfully")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}

