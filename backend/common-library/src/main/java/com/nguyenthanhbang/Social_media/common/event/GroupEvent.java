package com.nguyenthanhbang.Social_media.common.event;

import com.nguyenthanhbang.Social_media.common.enumeration.GroupPrivacy;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupEvent {
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
