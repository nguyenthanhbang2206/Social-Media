package com.nguyenthanhbang.Social_media.common.event;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PostDeletedEvent {
    private Long postId;
}
