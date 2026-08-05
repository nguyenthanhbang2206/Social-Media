package com.nguyenthanhbang.Social_media.common.event;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class FriendEvent {
    @Builder.Default
    private String eventId = java.util.UUID.randomUUID().toString();
    @Builder.Default
    private java.time.Instant timestamp = java.time.Instant.now();
    private String action;

    private Long actorId;

    private String actorName;

    private String actorAvatar;

    private Long recipientId;
}
