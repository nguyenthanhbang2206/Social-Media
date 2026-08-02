package com.nguyenthanhbang.Social_media.repository;

import com.nguyenthanhbang.Social_media.model.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);
    List<PostLike> findByUserIdAndPostIdIn(Long userId, List<Long> postIds);
    List<PostLike> findByPostId(Long postId);
    long countByPostId(Long postId);
    void deleteByPostId(Long postId);
}
