package com.nguyenthanhbang.Social_media.dto.request;

import com.nguyenthanhbang.Social_media.enumeration.GroupPrivacy;
import lombok.Getter;

@Getter
public class GroupRequest {
    private String name;
    private String description;
    private String groupImage;
    private String coverImage;
    private GroupPrivacy privacy;
}
