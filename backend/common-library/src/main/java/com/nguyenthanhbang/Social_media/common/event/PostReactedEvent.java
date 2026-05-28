package com.nguyenthanhbang.Social_media.common.event;

import com.nguyenthanhbang.Social_media.common.enumeration.ReactionType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostReactedEvent {
    private Long postId;
    private Long actorId;
    private String actorName;
    private ReactionType reactionType;
    private Long ownerId;
}
