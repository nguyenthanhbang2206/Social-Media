package com.nguyenthanhbang.Social_media.controller.internal;

import com.nguyenthanhbang.Social_media.common.dto.ApiResponse;
import com.nguyenthanhbang.Social_media.common.dto.PostInteractionCountResponse;
import com.nguyenthanhbang.Social_media.repository.CommentRepository;
import com.nguyenthanhbang.Social_media.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/posts")
@RequiredArgsConstructor
public class InternalPostInteractionController {
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

    @GetMapping("/{postId}/counts")
    public ResponseEntity<ApiResponse<PostInteractionCountResponse>> getCounts(@PathVariable Long postId) {
        PostInteractionCountResponse counts = PostInteractionCountResponse.builder()
                .totalComments(commentRepository.countByPostId(postId))
                .totalReactions(postLikeRepository.countByPostId(postId))
                .build();
        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Get post counts successfully")
                .data(counts)
                .build();
        return ResponseEntity.ok(response);
    }
}
