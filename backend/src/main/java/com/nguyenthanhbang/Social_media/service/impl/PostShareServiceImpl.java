package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.dto.request.PostShareRequest;
import com.nguyenthanhbang.Social_media.model.Post;
import com.nguyenthanhbang.Social_media.model.PostShare;
import com.nguyenthanhbang.Social_media.model.User;
import com.nguyenthanhbang.Social_media.repository.PostRepository;
import com.nguyenthanhbang.Social_media.repository.PostShareRepository;
import com.nguyenthanhbang.Social_media.service.PostService;
import com.nguyenthanhbang.Social_media.service.PostShareService;
import com.nguyenthanhbang.Social_media.service.UserService;
import com.nguyenthanhbang.Social_media.service.BlockService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostShareServiceImpl implements PostShareService {
    private final PostShareRepository postShareRepository;
    private final PostService postService;
    private final UserService userService;
    private final PostRepository postRepository;
    private final BlockService blockService;

    @Override
    public PostShare sharePost(Long postId, PostShareRequest request) {
        User user = userService.getUserLogin();
        Post post = postService.getPostById(postId);
        blockService.ensureNotBlocked(post.getUser().getId());
        PostShare postShare = PostShare.builder()
                .shareContent(request.getShareContent())
                .user(user)
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
        User user = userService.getUserLogin();
        PostShare postShare = postShareRepository.findByIdAndPostId(shareId, postId)
                .orElseThrow(() -> new EntityNotFoundException("Share not found"));
        if (!postShare.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("You do not have permission to delete this share");
        }
        Post post = postShare.getPost();
        postShareRepository.delete(postShare);
        post.setTotalShares(Math.max(0L, post.getTotalShares() - 1));
        postRepository.save(post);
    }
}
