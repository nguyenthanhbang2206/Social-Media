package com.nguyenthanhbang.Social_media.dto.response;

import com.nguyenthanhbang.Social_media.enumeration.GroupPrivacy;
import com.nguyenthanhbang.Social_media.model.GroupMember;
import com.nguyenthanhbang.Social_media.model.GroupPost;
import com.nguyenthanhbang.Social_media.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
