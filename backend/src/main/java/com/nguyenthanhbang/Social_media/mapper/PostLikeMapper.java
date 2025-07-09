package com.nguyenthanhbang.Social_media.mapper;

import com.nguyenthanhbang.Social_media.dto.response.PostLikeResponse;
import com.nguyenthanhbang.Social_media.model.PostLike;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostLikeMapper {
    PostLikeResponse toPostLikeResponse(PostLike postLike);
    List<PostLikeResponse> toPostLikeResponses(List<PostLike> postLikes);
}
