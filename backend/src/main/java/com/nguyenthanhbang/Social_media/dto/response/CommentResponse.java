package com.nguyenthanhbang.Social_media.dto.response;

import com.nguyenthanhbang.Social_media.model.Comment;
import com.nguyenthanhbang.Social_media.model.CommentLike;
import com.nguyenthanhbang.Social_media.model.Post;
import com.nguyenthanhbang.Social_media.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CommentResponse extends BaseResponse{
    private String content;
    private UserResponse user;
    private CommentResponse parentComment;
    private Long parentCommentId;
    private List<CommentLikeResponse> likes;
}
