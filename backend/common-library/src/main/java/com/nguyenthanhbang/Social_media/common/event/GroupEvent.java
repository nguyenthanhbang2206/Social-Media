package com.nguyenthanhbang.Social_media.common.event;

import com.nguyenthanhbang.Social_media.common.enumeration.GroupPrivacy;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class GroupEvent {
    @Builder.Default
    private String eventId = java.util.UUID.randomUUID().toString();
    @Builder.Default
    private java.time.Instant timestamp = java.time.Instant.now();
    private String name;
    private String groupImage;
    private GroupPrivacy privacy = GroupPrivacy.PUBLIC;
    private Long ownerId;
    private GroupEventType eventType;
    private Long recipientId;
    private Long actorId;
    private String actorName;
    public enum GroupEventType{
        APPROVED, REQUESTED, LEFT
    }
}
