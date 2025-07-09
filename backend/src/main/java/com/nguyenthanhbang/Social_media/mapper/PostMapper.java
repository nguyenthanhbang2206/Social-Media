package com.nguyenthanhbang.Social_media.mapper;

import com.nguyenthanhbang.Social_media.dto.response.PostResponse;
import com.nguyenthanhbang.Social_media.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PostMediaMapper.class})
public interface PostMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "modifiedDate", source = "modifiedDate")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "modifiedBy", source = "modifiedBy")
    PostResponse toPostResponse(Post post);
    List<PostResponse> toPostResponses(List<Post> posts);
}
