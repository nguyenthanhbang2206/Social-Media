package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.client.UserClient;
import com.nguyenthanhbang.Social_media.common.dto.ApiResponse;
import com.nguyenthanhbang.Social_media.dto.request.PostShareRequest;
import com.nguyenthanhbang.Social_media.common.util.RequestHeaderUtil;
import com.nguyenthanhbang.Social_media.common.dto.UserSummaryResponse;
import com.nguyenthanhbang.Social_media.model.Post;
import com.nguyenthanhbang.Social_media.model.PostShare;
import com.nguyenthanhbang.Social_media.repository.PostRepository;
import com.nguyenthanhbang.Social_media.repository.PostShareRepository;
import com.nguyenthanhbang.Social_media.service.PostService;
import com.nguyenthanhbang.Social_media.service.PostShareService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostShareServiceImpl implements PostShareService {
    private final PostShareRepository postShareRepository;
    private final PostService postService;
    private final PostRepository postRepository;
    private final UserClient userClient;

    @Override
    public PostShare sharePost(Long postId, PostShareRequest request) {
        Long userId = getCurrentUserId();
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
        Long userId = getCurrentUserId();
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

    private Long getCurrentUserId() {
        String email = RequestHeaderUtil.getUserEmail()
                .orElseThrow(() -> new EntityNotFoundException("User not found - X-User-Email header missing"));
        
        ApiResponse<UserSummaryResponse> response = userClient.getUserByEmail(email);
        if (response == null || response.getData() == null) {
            throw new EntityNotFoundException("User not found with email: " + email);
        }
        
        return response.getData().getId();
    }
}
