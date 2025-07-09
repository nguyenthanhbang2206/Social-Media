package com.nguyenthanhbang.Social_media.mapper;

import com.nguyenthanhbang.Social_media.dto.response.PostMediaResponse;
import com.nguyenthanhbang.Social_media.model.PostMedia;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMediaMapper {
    PostMediaResponse toPostMediaResponse(PostMedia postMedia);
    List<PostMediaResponse> toPostMediaResponses(List<PostMedia> postMedia);
}
