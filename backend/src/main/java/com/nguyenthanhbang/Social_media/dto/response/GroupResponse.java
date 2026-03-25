package com.nguyenthanhbang.Social_media.dto.response;

import com.nguyenthanhbang.Social_media.enumeration.GroupPrivacy;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupResponse extends BaseResponse {
    private String name;
    private String description;
    private String groupImage;
    private String coverImage;
    private GroupPrivacy privacy;
    private UserResponse creator;
}
