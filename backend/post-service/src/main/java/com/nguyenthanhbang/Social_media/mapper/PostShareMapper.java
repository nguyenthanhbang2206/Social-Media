package com.nguyenthanhbang.Social_media.mapper;

import com.nguyenthanhbang.Social_media.dto.response.PostShareResponse;
import com.nguyenthanhbang.Social_media.model.PostShare;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostShareMapper {
    @Mapping(source = "post.id", target = "postId")
    PostShareResponse toPostShareResponse(PostShare postShare);
    List<PostShareResponse> toPostShareResponses(List<PostShare> postShares);
}

