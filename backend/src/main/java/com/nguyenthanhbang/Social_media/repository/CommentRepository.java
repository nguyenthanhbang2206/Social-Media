package com.nguyenthanhbang.Social_media.repository;

import com.nguyenthanhbang.Social_media.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    long countByPostId(Long postId);
    List<Comment> findByPostId(Long postId);
}
