package com.nguyenthanhbang.Social_media.common.event;

import com.nguyenthanhbang.Social_media.common.enumeration.ReactionType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PostReactedEvent {
    @Builder.Default
    private String eventId = java.util.UUID.randomUUID().toString();
    @Builder.Default
    private java.time.Instant timestamp = java.time.Instant.now();
    private Long postId;
    private Long actorId;
    private String actorName;
    private ReactionType reactionType;
    private Long ownerId;
}
