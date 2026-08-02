package com.nguyenthanhbang.Social_media.common.dto;

import com.nguyenthanhbang.Social_media.common.enumeration.GroupPrivacy;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupSummaryResponse extends BaseResponse {
    private String name;
    private GroupPrivacy privacy;
    private Long creatorId;
}
