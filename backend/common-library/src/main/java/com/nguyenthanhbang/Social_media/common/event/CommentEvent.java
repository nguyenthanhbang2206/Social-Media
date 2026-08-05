package com.nguyenthanhbang.Social_media.common.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CommentEvent {
    @Builder.Default
    private String eventId = java.util.UUID.randomUUID().toString();

    @Builder.Default
    private java.time.Instant timestamp = java.time.Instant.now();

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
