package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.dto.request.PostLikeRequest;
import com.nguyenthanhbang.Social_media.repository.PostLikeRepository;
import com.nguyenthanhbang.Social_media.model.Post;
import com.nguyenthanhbang.Social_media.model.PostLike;
import com.nguyenthanhbang.Social_media.model.User;
import com.nguyenthanhbang.Social_media.repository.PostRepository;
import com.nguyenthanhbang.Social_media.service.PostLikeService;
import com.nguyenthanhbang.Social_media.service.PostService;
import com.nguyenthanhbang.Social_media.service.UserService;
import com.nguyenthanhbang.Social_media.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostService postService;
    private final UserService userService;
    private final PostRepository postRepository;
    private final BlockService blockService;

    @Override
    public PostLike reactPost(PostLikeRequest request, Long postId) {
        User user = userService.getUserLogin();
        Post post = postService.getPostById(postId);
        blockService.ensureNotBlocked(post.getUser().getId());
        PostLike postLike = this.getReactByUserIdAndPostId(postId);
        boolean isNewReaction = postLike == null;
        if(isNewReaction) {
            postLike = new PostLike();
            postLike.setUser(user);
            postLike.setPost(post);
        }
        postLike.setReactionType(request.getReactionType());
        if (isNewReaction) {
            post.setTotalReactions(post.getTotalReactions() + 1);
            postRepository.save(post);
        }
        return postLikeRepository.save(postLike);
    }

    @Override
    public void deleteReactPost(Long postId) {
        Post post = postService.getPostById(postId);
        blockService.ensureNotBlocked(post.getUser().getId());
        PostLike postLike = this.getReactByUserIdAndPostId(postId);
        if (postLike == null) {
            throw new EntityNotFoundException("Reaction not found");
        }
        postLikeRepository.delete(postLike);
        post.setTotalReactions(Math.max(0L, post.getTotalReactions() - 1));
        postRepository.save(post);
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
