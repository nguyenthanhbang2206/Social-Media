package com.nguyenthanhbang.Social_media.mapper;

import com.nguyenthanhbang.Social_media.dto.response.CommentLikeResponse;
import com.nguyenthanhbang.Social_media.model.CommentLike;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentLikeMapper {
    @Mapping(source = "user.fullName", target = "username")
    CommentLikeResponse toCommentLikeResponse(CommentLike commentLike);
    List<CommentLikeResponse> toCommentLikeResponses(List<CommentLike> commentLikes);
}

