package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.dto.request.PostLikeRequest;
import com.nguyenthanhbang.Social_media.repository.PostLikeRepository;
import com.nguyenthanhbang.Social_media.model.Post;
import com.nguyenthanhbang.Social_media.model.PostLike;
import com.nguyenthanhbang.Social_media.model.User;
import com.nguyenthanhbang.Social_media.service.PostLikeService;
import com.nguyenthanhbang.Social_media.service.PostService;
import com.nguyenthanhbang.Social_media.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostService postService;
    private final UserService userService;
    @Override
    public PostLike reactPost(PostLikeRequest request, Long postId) {
        User user = userService.getUserLogin();
        Post post = postService.getPostById(postId);
        PostLike postLike = this.getReactByUserIdAndPostId(postId);
        if(postLike == null) {
            postLike = new PostLike();
            postLike.setReactionType(request.getReactionType());
            postLike.setUser(user);
            postLike.setPost(post);
        }else {
            postLike.setReactionType(request.getReactionType());
        }
        return postLikeRepository.save(postLike);
    }

    @Override
    public void deleteReactPost(Long postId) {
        Post post = postService.getPostById(postId);
        PostLike postLike = this.getReactByUserIdAndPostId(postId);
        postLikeRepository.delete(postLike);
    }

    @Override
    public PostLike getReactByUserIdAndPostId(Long postId) {
        User user = userService.getUserLogin();
        Optional<PostLike> postLike = postLikeRepository.findByUserIdAndPostId(user.getId(), postId);
        if(postLike.isPresent()){
            return postLike.get();
        }
        return null;
    }

    @Override
    public List<PostLike> getReactByPost(Long postId) {
        Post post = postService.getPostById(postId);
        return postLikeRepository.findByPostId(postId);
    }
}
