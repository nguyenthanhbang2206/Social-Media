package com.nguyenthanhbang.Social_media.repository;

import com.nguyenthanhbang.Social_media.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailAndActiveTrue(String email);
    List<User> findByActiveTrue();
    List<User> findByActiveFalse();
    @Query("SELECT a FROM User a WHERE a.active = true AND (LOWER(a.fullName) LIKE CONCAT('%', LOWER(:keyword), '%') OR LOWER(a.email) LIKE CONCAT('%', LOWER(:keyword), '%'))")
    List<User> search(@Param("keyword") String keyword);

    Optional<User> findByKeycloakId(UUID keycloakId);

    boolean existsByEmail(String email);
    boolean existsByUsername(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

}
