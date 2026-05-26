package com.nguyenthanhbang.Social_media.controller.user;

import com.nguyenthanhbang.Social_media.dto.request.CreatePostRequest;
import com.nguyenthanhbang.Social_media.dto.request.UpdatePostRequest;
import com.nguyenthanhbang.Social_media.common.dto.ApiResponse;
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
        Post post = postService.createPost(null, request);
        ApiResponse response = ApiResponse.builder()
                .message("Create post successfully")
                .status(HttpStatus.CREATED.value())
                .data(postMapper.toPostResponse(post))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PutMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(@RequestBody UpdatePostRequest request, @PathVariable("id") Long id){
        Post post = postService.updatePost(null, id, request);
        ApiResponse response = ApiResponse.builder()
                .message("Update post successfully")
                .status(HttpStatus.OK.value())
                .data(postMapper.toPostResponse(post))
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPostByUser(@PathVariable("userId") Long userId){
        List<Post> posts = postService.getPostByUserId(userId);
        List<PostResponse> responses = postMapper.toPostResponses(posts);
        ApiResponse response = ApiResponse.builder()
                .message("Get post by user successfully")
                .status(HttpStatus.OK.value())
            .data(responses)
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPosts(){
        List<Post> posts = postService.getNewsFeed();
        List<PostResponse> responses = postMapper.toPostResponses(posts);
        ApiResponse response = ApiResponse.builder()
                .message("Get posts successfully")
                .status(HttpStatus.OK.value())
            .data(responses)
                .build();
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable("id") Long id){
        postService.deletePost(id);
        ApiResponse response = ApiResponse.builder()
                .message("Delete post successfully")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getPostById(@PathVariable("id") Long id){
        Post post = postService.getPostById(id);
        PostResponse postResponse = postMapper.toPostResponse(post);
        ApiResponse response = ApiResponse.builder()
                .message("Get post successfully")
                .status(HttpStatus.OK.value())
                .data(postResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/groups/{groupId}/posts")
    public ResponseEntity<ApiResponse<PostResponse>> createPostGroup(@PathVariable("groupId") Long groupId,
                                                                     @RequestBody CreatePostRequest request){
        Post post = postService.createPost(groupId, request);
        ApiResponse response = ApiResponse.builder()
                .message("Creat post group successfully")
                .status(HttpStatus.CREATED.value())
                .data(postMapper.toPostResponse(post))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/groups/{groupId}/posts")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPostApprovedForGroup(@PathVariable("groupId") Long groupId){
        List<Post> posts = postService.getPostApprovedForGroup(groupId);
        ApiResponse response = ApiResponse.builder()
                .message("Get post approved group successfully")
                .status(HttpStatus.OK.value())
                .data(postMapper.toPostResponses(posts))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/groups/{groupId}/posts/pending")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPostPending(@PathVariable("groupId") Long groupId){
        List<Post> posts = postService.getPostPending(groupId);
        ApiResponse response = ApiResponse.builder()
                .message("Get post pending successfully")
                .status(HttpStatus.OK.value())
                .data(postMapper.toPostResponses(posts))
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/groups/{groupId}/posts/{postId}/approve")
    public ResponseEntity<ApiResponse<Void>> approvePost(@PathVariable("groupId") Long groupId, @PathVariable("postId") Long postId){
        postService.approvePost(groupId, postId);
        ApiResponse response = ApiResponse.builder()
                .message("Approve post successfully")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/groups/{groupId}/posts/{postId}/pin")
    public ResponseEntity<ApiResponse<Void>> pinPost(@PathVariable("groupId") Long groupId, @PathVariable("postId") Long postId) {
        postService.pinPost(groupId, postId);
        ApiResponse response = ApiResponse.builder()
                .message("Pin post successfully")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/groups/{groupId}/posts/{postId}/unpin")
    public ResponseEntity<ApiResponse<Void>> unpinPost(@PathVariable("groupId") Long groupId, @PathVariable("postId") Long postId) {
        postService.unpinPost(groupId, postId);
        ApiResponse response = ApiResponse.builder()
                .message("Unpin post successfully")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}
