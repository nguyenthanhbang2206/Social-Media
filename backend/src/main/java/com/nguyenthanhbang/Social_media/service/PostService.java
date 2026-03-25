package com.nguyenthanhbang.Social_media.service;

import com.nguyenthanhbang.Social_media.dto.request.CreatePostRequest;
import com.nguyenthanhbang.Social_media.dto.request.UpdatePostRequest;
import com.nguyenthanhbang.Social_media.model.Post;
import com.nguyenthanhbang.Social_media.model.PostLike;

import java.util.List;

public interface PostService {
    Post createPost(Long groupId, CreatePostRequest request);
    Post updatePost(Long groupId, Long postId, UpdatePostRequest request);
    List<Post> getPostApprovedForGroup(Long groupId);
    List<Post> getPostPending(Long groupId);
    void approvePost(Long groupId, Long postId);
    List<Post> getNewsFeed();
    List<Post> getPostByUserId(Long userId);
    Post getPostById(Long postId);
    void deletePost(Long postId);

}
