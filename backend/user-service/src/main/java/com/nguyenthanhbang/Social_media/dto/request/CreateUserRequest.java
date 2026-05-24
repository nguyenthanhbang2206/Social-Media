package com.nguyenthanhbang.Social_media.dto.request;

import com.nguyenthanhbang.Social_media.common.enumeration.Role;
import lombok.Getter;

@Getter
public class CreateUserRequest {
    private String email;
    private String password;
    private String confirmPassword;
    private String fullName;
    private Role role;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public String getFullName() {
        return fullName;
    }

    public Role getRole() {
        return role;
    }
}