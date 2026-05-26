package com.nguyenthanhbang.Social_media.controller.user;

import com.nguyenthanhbang.Social_media.dto.request.PostLikeRequest;
import com.nguyenthanhbang.Social_media.common.dto.ApiResponse;
import com.nguyenthanhbang.Social_media.dto.response.PostLikeResponse;
import com.nguyenthanhbang.Social_media.mapper.PostLikeMapper;
import com.nguyenthanhbang.Social_media.model.PostLike;
import com.nguyenthanhbang.Social_media.service.PostLikeService;
import com.nguyenthanhbang.Social_media.common.dto.PostInteractionCountResponse;
import com.nguyenthanhbang.Social_media.repository.CommentRepository;
import com.nguyenthanhbang.Social_media.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class PostLikeController {
    private final PostLikeService postLikeService;
    private final PostLikeMapper postLikeMapper;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

    @PostMapping("/posts/{postId}/react")
    public ResponseEntity<ApiResponse<PostLikeResponse>> reactPost(@RequestBody PostLikeRequest request, @PathVariable("postId") Long postId) {
        PostLike postLike = postLikeService.reactPost(request, postId);
        ApiResponse response = ApiResponse.builder()
                .message("React post successfully")
                .status(HttpStatus.CREATED.value())
                .data(postLikeMapper.toPostLikeResponse(postLike))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/posts/{postId}/reactions")
    public ResponseEntity<ApiResponse<List<PostLikeResponse>>> getReactionsOfPost(@PathVariable("postId") Long postId) {
        List<PostLike> postLikes = postLikeService.getReactByPost(postId);
        ApiResponse response = ApiResponse.builder()
                .message("Get react post successfully")
                .status(HttpStatus.OK.value())
                .data(postLikeMapper.toPostLikeResponses(postLikes))
                .build();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/posts/{postId}/me")
    public ResponseEntity<ApiResponse<PostLikeResponse>> getReactPostOfUser(@PathVariable("postId") Long postId) {
        PostLike postLike = postLikeService.getReactByUserIdAndPostId(postId);
        ApiResponse response = ApiResponse.builder()
                .message("Get react post successfully")
                .status(HttpStatus.OK.value())
                .data(postLikeMapper.toPostLikeResponse(postLike))
                .build();
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/posts/{postId}/un-react")
    public ResponseEntity<ApiResponse<Void>> deleteReactPost(@PathVariable("postId") Long postId){
        postLikeService.deleteReactPost(postId);
        ApiResponse response = ApiResponse.builder()
                .message("Delete react post successfu lly")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts/{postId}/counts")
    public ResponseEntity<ApiResponse<PostInteractionCountResponse>> getCounts(@PathVariable("postId") Long postId) {
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
