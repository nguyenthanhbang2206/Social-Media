package com.nguyenthanhbang.Social_media.service;

import com.nguyenthanhbang.Social_media.dto.request.CreateUserRequest;
import com.nguyenthanhbang.Social_media.dto.request.UpdateUserRequest;
import com.nguyenthanhbang.Social_media.model.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User createUser(CreateUserRequest user);
    User getUserByEmail(String email);
    User getUserLogin();
    User updateProfile(UpdateUserRequest request);
    List<User> getAllUsers(Boolean active);
    List<User> getActiveUsers();
    User changeStatus(Long id);
    User getUserById(Long id);
    List<User> searchUser(String keyword);
    User syncUserFromKeycloak(CreateUserRequest request);
}
