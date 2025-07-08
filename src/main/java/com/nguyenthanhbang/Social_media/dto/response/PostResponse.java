package com.nguyenthanhbang.Social_media.dto.response;

import com.nguyenthanhbang.Social_media.enumeration.PrivacyLevel;
import com.nguyenthanhbang.Social_media.model.*;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class PostResponse {
    private Boolean active;
    private String content;
    private PrivacyLevel privacy;
}
