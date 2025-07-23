package com.nguyenthanhbang.Social_media.repository;

import com.nguyenthanhbang.Social_media.model.PostShare;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostShareRepository extends JpaRepository<PostShare, Long> {
    long countByPostId(Long postId);
}
