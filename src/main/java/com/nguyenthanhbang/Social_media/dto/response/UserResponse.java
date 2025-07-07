package com.nguyenthanhbang.Social_media.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nguyenthanhbang.Social_media.enumeration.Gender;
import com.nguyenthanhbang.Social_media.enumeration.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Builder
public class UserResponse {
    private String fullName;
    private String avatar;
    @Enumerated(value = EnumType.STRING)
    private Gender gender;
    private LocalDate dateOfBirth;
    private String coverPhoto;
}
