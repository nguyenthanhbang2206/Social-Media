package com.nguyenthanhbang.Social_media.controller.user;

import com.nguyenthanhbang.Social_media.dto.request.CreatePostRequest;
import com.nguyenthanhbang.Social_media.dto.request.UpdatePostRequest;
import com.nguyenthanhbang.Social_media.dto.response.ApiResponse;
import com.nguyenthanhbang.Social_media.dto.response.PostResponse;
import com.nguyenthanhbang.Social_media.mapper.PostMapper;
import com.nguyenthanhbang.Social_media.model.Post;
import com.nguyenthanhbang.Social_media.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;
    @PostMapping("/posts")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(@RequestBody CreatePostRequest request){
        Post post = postService.createPost(request);
        ApiResponse response = ApiResponse.builder()
                .message("Create post successfully")
                .status(HttpStatus.CREATED.value())
                .data(postMapper.toPostResponse(post))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PutMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(@RequestBody UpdatePostRequest request, @PathVariable Long id){
        Post post = postService.updatePost(id, request);
        ApiResponse response = ApiResponse.builder()
                .message("Update post successfully")
                .status(HttpStatus.OK.value())
                .data(postMapper.toPostResponse(post))
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPostByUser(@PathVariable Long userId){
        List<Post> posts = postService.getPostByUserId(userId);
        ApiResponse response = ApiResponse.builder()
                .message("Get post successfully")
                .status(HttpStatus.OK.value())
                .data(postMapper.toPostResponses(posts))
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPosts(){
        List<Post> posts = postService.getNewsFeed();
        ApiResponse response = ApiResponse.builder()
                .message("Get posts successfully")
                .status(HttpStatus.OK.value())
                .data(postMapper.toPostResponses(posts))
                .build();
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long id){
        postService.deletePost(id);
        ApiResponse response = ApiResponse.builder()
                .message("Delete post successfully")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getPostById(@PathVariable Long id){
        Post post = postService.getPostById(id);
        ApiResponse response = ApiResponse.builder()
                .message("Get post successfully")
                .status(HttpStatus.OK.value())
                .data(postMapper.toPostResponse(post))
                .build();
        return ResponseEntity.ok(response);
    }
}
