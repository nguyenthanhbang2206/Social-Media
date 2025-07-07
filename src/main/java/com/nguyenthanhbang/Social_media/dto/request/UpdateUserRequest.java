package com.nguyenthanhbang.Social_media.dto.request;


import com.nguyenthanhbang.Social_media.enumeration.Gender;
import lombok.Getter;

@Getter
public class UpdateUserRequest {
    private String fullName;
    private Gender gender;
}
