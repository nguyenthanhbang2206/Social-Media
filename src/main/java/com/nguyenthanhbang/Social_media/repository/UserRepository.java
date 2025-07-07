package com.nguyenthanhbang.Social_media.repository;

import com.nguyenthanhbang.Social_media.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailAndActiveTrue(String email);
    User findByRefreshTokenAndEmailAndActiveTrue(String refreshToken, String email);
    List<User> findByActiveTrue();
    List<User> findByActiveFalse();
}
