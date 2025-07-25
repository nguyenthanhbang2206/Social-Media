package com.nguyenthanhbang.Social_media.repository;

import com.nguyenthanhbang.Social_media.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailAndActiveTrue(String email);
    User findByRefreshTokenAndEmailAndActiveTrue(String refreshToken, String email);
    List<User> findByActiveTrue();
    List<User> findByActiveFalse();
    @Query("SELECT a FROM User a WHERE a.active = true AND (LOWER(a.fullName) LIKE CONCAT('%', LOWER(:keyword), '%') OR LOWER(a.email) LIKE CONCAT('%', LOWER(:keyword), '%'))")
    List<User> search(@Param("keyword") String keyword);

}
