package com.nguyenthanhbang.Social_media.common.event;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class FriendEvent {
    private String action;

    private Long actorId;

    private String actorName;

    private String actorAvatar;

    private Long recipientId;

}
