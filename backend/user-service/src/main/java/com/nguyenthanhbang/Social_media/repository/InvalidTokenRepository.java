package com.nguyenthanhbang.Social_media.repository;

import com.nguyenthanhbang.Social_media.model.InvalidToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidTokenRepository extends JpaRepository<InvalidToken, Long> {
    boolean existsByToken(String token);
}
