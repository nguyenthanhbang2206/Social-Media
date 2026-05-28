package com.nguyenthanhbang.Social_media.common.dto;

import com.nguyenthanhbang.Social_media.common.enumeration.Gender;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
public class UserSummaryResponse extends BaseResponse {
    private String fullName;
    private String avatar;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String coverPhoto;
}
