package com.nguyenthanhbang.Social_media.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostInteractionCountResponse {
    private Long totalComments;
    private Long totalReactions;
}
