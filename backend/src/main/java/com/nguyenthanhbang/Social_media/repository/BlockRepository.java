package com.nguyenthanhbang.Social_media.repository;

import com.nguyenthanhbang.Social_media.model.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {
    Optional<Block> findByBlockerIdAndBlockedId(Long blockerId, Long blockedId);
    List<Block> findByBlockerId(Long blockerId);

    @Query("SELECT COUNT(b) > 0 FROM Block b WHERE (b.blocker.id = :userId1 AND b.blocked.id = :userId2) OR (b.blocker.id = :userId2 AND b.blocked.id = :userId1)")
    boolean existsBlockBetween(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}

