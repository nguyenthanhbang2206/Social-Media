package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.dto.request.PostShareRequest;
import com.nguyenthanhbang.Social_media.common.util.RequestHeaderUtil;
import com.nguyenthanhbang.Social_media.model.Post;
import com.nguyenthanhbang.Social_media.model.PostShare;
import com.nguyenthanhbang.Social_media.repository.PostRepository;
import com.nguyenthanhbang.Social_media.repository.PostShareRepository;
import com.nguyenthanhbang.Social_media.service.PostService;
import com.nguyenthanhbang.Social_media.service.PostShareService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostShareServiceImpl implements PostShareService {
    private final PostShareRepository postShareRepository;
    private final PostService postService;
    private final PostRepository postRepository;

    @Override
    public PostShare sharePost(Long postId, PostShareRequest request) {
        Long userId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        Post post = postService.getPostById(postId);
        PostShare postShare = PostShare.builder()
                .shareContent(request.getShareContent())
            .userId(userId)
                .post(post)
                .build();
        post.setTotalShares(post.getTotalShares() + 1);
        postRepository.save(post);
        return postShareRepository.save(postShare);
    }

    @Override
    public List<PostShare> getSharesByPost(Long postId) {
        postService.getPostById(postId);
        return postShareRepository.findByPostId(postId);
    }

    @Override
    public void deleteShare(Long postId, Long shareId) {
        Long userId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        PostShare postShare = postShareRepository.findByIdAndPostId(shareId, postId)
                .orElseThrow(() -> new EntityNotFoundException("Share not found"));
        if (!postShare.getUserId().equals(userId)) {
            throw new IllegalStateException("You do not have permission to delete this share");
        }
        Post post = postShare.getPost();
        postShareRepository.delete(postShare);
        post.setTotalShares(Math.max(0L, post.getTotalShares() - 1));
        postRepository.save(post);
    }
}
