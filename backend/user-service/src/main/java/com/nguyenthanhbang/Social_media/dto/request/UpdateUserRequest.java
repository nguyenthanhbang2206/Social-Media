package com.nguyenthanhbang.Social_media.dto.request;


import com.nguyenthanhbang.Social_media.common.enumeration.Gender;
import lombok.Getter;

@Getter
public class UpdateUserRequest {
    private String fullName;
    private Gender gender;

    public String getFullName() {
        return fullName;
    }

    public Gender getGender() {
        return gender;
    }
}
