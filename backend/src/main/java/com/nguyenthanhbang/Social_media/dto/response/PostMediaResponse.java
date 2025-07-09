package com.nguyenthanhbang.Social_media.dto.response;

import com.nguyenthanhbang.Social_media.enumeration.MediaType;
import com.nguyenthanhbang.Social_media.model.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Getter
@Setter
public class PostMediaResponse extends BaseResponse{
    private String mediaUrl;
    private MediaType mediaType;
    private Integer uploadOrder;
}
