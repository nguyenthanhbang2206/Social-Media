package com.nguyenthanhbang.Social_media.dto.response;

import com.nguyenthanhbang.Social_media.common.dto.BaseResponse;
import com.nguyenthanhbang.Social_media.common.enumeration.MediaType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostMediaResponse extends BaseResponse{
    private String mediaUrl;
    private MediaType mediaType;
    private Integer uploadOrder;
}
