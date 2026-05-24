package com.nguyenthanhbang.Social_media.repository;

import com.nguyenthanhbang.Social_media.model.PostShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostShareRepository extends JpaRepository<PostShare, Long> {
    long countByPostId(Long postId);
    List<PostShare> findByPostId(Long postId);
    Optional<PostShare> findByIdAndPostId(Long id, Long postId);
}
