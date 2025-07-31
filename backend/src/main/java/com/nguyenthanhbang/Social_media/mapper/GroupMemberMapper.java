package com.nguyenthanhbang.Social_media.mapper;

import com.nguyenthanhbang.Social_media.dto.response.BaseResponse;
import com.nguyenthanhbang.Social_media.dto.response.GroupMemberResponse;
import com.nguyenthanhbang.Social_media.model.GroupMember;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface GroupMemberMapper {
    GroupMemberResponse toGroupMemberResponse(GroupMember groupMember);
    List<GroupMemberResponse> toGroupMemberResponses(List<GroupMember> groupMembers);
}
