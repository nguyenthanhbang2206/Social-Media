package com.nguyenthanhbang.Social_media.mapper;

import com.nguyenthanhbang.Social_media.dto.response.CommentResponse;
import com.nguyenthanhbang.Social_media.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {
    @Mapping(source = "parentComment.id", target = "parentCommentId")
    CommentResponse toCommentResponse(Comment comment);
    List<CommentResponse> toCommentResponses(List<Comment> comments);
}
