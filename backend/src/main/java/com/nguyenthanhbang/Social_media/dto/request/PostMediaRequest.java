package com.nguyenthanhbang.Social_media.dto.request;

import com.nguyenthanhbang.Social_media.enumeration.MediaType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostMediaRequest {
    private String mediaUrl;
    private MediaType mediaType;
    private Integer uploadOrder;
}
