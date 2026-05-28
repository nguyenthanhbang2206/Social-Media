package com.nguyenthanhbang.Social_media.common.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentEvent {
    private Long commentId;
    private Long postId;
    private Long actorId;
    private String actorName;
    private Long recipientId;
    private Long parentCommentId;
    private String commentPreview;
    private boolean reply;
    private String action;
}
