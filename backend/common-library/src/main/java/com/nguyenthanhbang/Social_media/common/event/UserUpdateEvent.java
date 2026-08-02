package com.nguyenthanhbang.Social_media.common.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserUpdateEvent {
    private Long userId;
    private String fullName;
    private Boolean active;
}
