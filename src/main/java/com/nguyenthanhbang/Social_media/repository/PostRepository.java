package com.nguyenthanhbang.Social_media.repository;

import com.nguyenthanhbang.Social_media.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
