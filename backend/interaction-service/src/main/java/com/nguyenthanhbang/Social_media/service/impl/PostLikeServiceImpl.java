package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.client.PostClient;
import com.nguyenthanhbang.Social_media.client.UserClient;
import com.nguyenthanhbang.Social_media.common.util.RequestHeaderUtil;
import com.nguyenthanhbang.Social_media.dto.request.PostLikeRequest;
import com.nguyenthanhbang.Social_media.common.dto.PostSummaryResponse;
import com.nguyenthanhbang.Social_media.model.PostLike;
import com.nguyenthanhbang.Social_media.repository.PostLikeRepository;
import com.nguyenthanhbang.Social_media.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostClient postClient;
    private final UserClient userClient;

    @Override
    public PostLike reactPost(PostLikeRequest request, Long postId) {
        Long userId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        PostSummaryResponse post = postClient.getPostById(postId).getData();
        if (post == null) {
            throw new EntityNotFoundException("Post not found");
        }
        boolean blocked = Boolean.TRUE.equals(userClient.existsBlock(userId, post.getUserId()).getData());
        if (blocked) {
            throw new EntityNotFoundException("Blocked user");
        }
        PostLike postLike = this.getReactByUserIdAndPostId(postId);
        boolean isNewReaction = postLike == null;
        if(isNewReaction) {
            postLike = new PostLike();
            postLike.setUserId(userId);
            postLike.setPostId(postId);
        }
        postLike.setReactionType(request.getReactionType());
        return postLikeRepository.save(postLike);
    }

    @Override
    public void deleteReactPost(Long postId) {
        Long userId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        PostSummaryResponse post = postClient.getPostById(postId).getData();
        if (post == null) {
            throw new EntityNotFoundException("Post not found");
        }
        boolean blocked = Boolean.TRUE.equals(userClient.existsBlock(userId, post.getUserId()).getData());
        if (blocked) {
            throw new EntityNotFoundException("Blocked user");
        }
        PostLike postLike = this.getReactByUserIdAndPostId(postId);
        if (postLike == null) {
            throw new EntityNotFoundException("Reaction not found");
        }
        postLikeRepository.delete(postLike);
    }

    @Override
    public PostLike getReactByUserIdAndPostId(Long postId) {
        Long userId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        Optional<PostLike> postLike = postLikeRepository.findByUserIdAndPostId(userId, postId);
        if(postLike.isPresent()){
            return postLike.get();
        }
        return null;
    }

    @Override
    public List<PostLike> getReactByPost(Long postId) {
        postClient.getPostById(postId);
        return postLikeRepository.findByPostId(postId);
    }
}
