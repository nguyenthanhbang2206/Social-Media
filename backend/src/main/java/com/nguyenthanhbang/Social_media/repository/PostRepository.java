package com.nguyenthanhbang.Social_media.repository;

import com.nguyenthanhbang.Social_media.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.expression.spel.ast.OpAnd;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId);
    List<Post> findByGroupIdAndIsApproved(Long groupId, Boolean isApproved);
    Optional<Post> findByGroupIdAndId(Long groupId, Long postId);
}
