package com.nguyenthanhbang.Social_media.dto.request;

import com.nguyenthanhbang.Social_media.common.enumeration.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
    private String email;
    private String password;
    private String confirmPassword;
    private String fullName;
    private Role role;
    private String username;
    private String keycloakUserId;
}