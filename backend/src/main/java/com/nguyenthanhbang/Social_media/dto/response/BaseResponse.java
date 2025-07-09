package com.nguyenthanhbang.Social_media.dto.response;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Getter
@Setter
public abstract class BaseResponse {
    private Long id;
    private Boolean active;
    private Instant createdDate;
    private Instant modifiedDate;
    private String createdBy;
    private String modifiedBy;
}
