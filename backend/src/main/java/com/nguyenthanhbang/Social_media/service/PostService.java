package com.nguyenthanhbang.Social_media.service;

import com.nguyenthanhbang.Social_media.dto.request.CreatePostRequest;
import com.nguyenthanhbang.Social_media.dto.request.UpdatePostRequest;
import com.nguyenthanhbang.Social_media.model.Post;
import com.nguyenthanhbang.Social_media.model.PostLike;

import java.util.List;

public interface PostService {
    Post createPost(CreatePostRequest request);
    Post updatePost(Long postId, UpdatePostRequest request);
    List<Post> getNewsFeed();
    List<Post> getPostByUserId(Long userId);
    Post getPostById(Long postId);
    void deletePost(Long postId);

}
