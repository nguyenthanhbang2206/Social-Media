package com.nguyenthanhbang.Social_media.controller.user;

import com.nguyenthanhbang.Social_media.dto.request.CreatePostRequest;
import com.nguyenthanhbang.Social_media.dto.request.PostLikeRequest;
import com.nguyenthanhbang.Social_media.dto.request.UpdatePostRequest;
import com.nguyenthanhbang.Social_media.dto.response.ApiResponse;
import com.nguyenthanhbang.Social_media.dto.response.PostLikeResponse;
import com.nguyenthanhbang.Social_media.dto.response.PostResponse;
import com.nguyenthanhbang.Social_media.mapper.PostLikeMapper;
import com.nguyenthanhbang.Social_media.model.Post;
import com.nguyenthanhbang.Social_media.model.PostLike;
import com.nguyenthanhbang.Social_media.service.PostLikeService;
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

    @PostMapping("/posts/{postId}/react")
    public ResponseEntity<ApiResponse<PostLikeResponse>> reactPost(@RequestBody PostLikeRequest request, @PathVariable Long postId) {
        PostLike postLike = postLikeService.reactPost(request, postId);
        ApiResponse response = ApiResponse.builder()
                .message("React post successfully")
                .status(HttpStatus.CREATED.value())
                .data(postLikeMapper.toPostLikeResponse(postLike))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/posts/{postId}/me")
    public ResponseEntity<ApiResponse<PostLikeResponse>> getReactPostOfUser(@PathVariable Long postId) {
        PostLike postLike = postLikeService.getReactByUserIdAndPostId(postId);
        ApiResponse response = ApiResponse.builder()
                .message("Get react post successfully")
                .status(HttpStatus.OK.value())
                .data(postLikeMapper.toPostLikeResponse(postLike))
                .build();
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/posts/{postId}/un-react")
    public ResponseEntity<ApiResponse<Void>> deleteReactPost(@PathVariable Long postId){
        postLikeService.deleteReactPost(postId);
        ApiResponse response = ApiResponse.builder()
                .message("Delete react post successfully")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

}
