package com.nguyenthanhbang.Social_media.dto.response;

import com.nguyenthanhbang.Social_media.common.dto.BaseResponse;
import com.nguyenthanhbang.Social_media.common.enumeration.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class UserResponse extends BaseResponse {
    private String fullName;
    private String avatar;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String coverPhoto;
}
