package com.nguyenthanhbang.Social_media.mapper;

import com.nguyenthanhbang.Social_media.dto.response.CommentLikeResponse;
import com.nguyenthanhbang.Social_media.model.CommentLike;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentLikeMapper {
    CommentLikeResponse toCommentLikeResponse(CommentLike commentLike);
    List<CommentLikeResponse> toCommentLikeResponses(List<CommentLike> commentLikes);
}

