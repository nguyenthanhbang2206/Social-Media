package com.nguyenthanhbang.Social_media.dto.request;

import com.nguyenthanhbang.Social_media.enumeration.Role;
import lombok.Getter;

@Getter
public class CreateUserRequest {
    private String email;
    private String password;
    private String confirmPassword;
    private String fullName;
    private Role role;
}