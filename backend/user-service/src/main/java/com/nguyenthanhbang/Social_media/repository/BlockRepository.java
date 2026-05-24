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

    @Query("SELECT COUNT(b) > 0 FROM Block b WHERE (b.blockerId = :userId1 AND b.blockedId = :userId2) OR (b.blockerId = :userId2 AND b.blockedId = :userId1)")
    boolean existsBlockBetween(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}

