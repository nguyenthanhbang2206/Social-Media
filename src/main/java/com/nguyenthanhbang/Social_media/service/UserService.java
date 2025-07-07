package com.nguyenthanhbang.Social_media.service;

import com.nguyenthanhbang.Social_media.dto.request.CreateUserRequest;
import com.nguyenthanhbang.Social_media.dto.request.UpdateUserRequest;
import com.nguyenthanhbang.Social_media.dto.response.UserResponse;
import com.nguyenthanhbang.Social_media.model.User;

import java.util.List;

public interface UserService {
    void updateTokenOfUser(String email, String refreshToken);
    User createUser(CreateUserRequest user);
    User getUserByRefreshTokenAndEmail(String refreshToken, String email);
    User getUserByEmail(String email);
    User getUserLogin();
    User updateProfile(UpdateUserRequest request);
    List<User> getAllUsers(Boolean active);
    List<UserResponse> getActiveUsers();
    User changeStatus(Long id);
    UserResponse getUserById(Long id);
}
