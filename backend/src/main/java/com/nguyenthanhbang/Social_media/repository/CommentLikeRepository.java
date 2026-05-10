package com.nguyenthanhbang.Social_media.repository;

import com.nguyenthanhbang.Social_media.model.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByUserIdAndCommentId(Long userId, Long commentId);
    List<CommentLike> findByCommentId(Long commentId);
}

