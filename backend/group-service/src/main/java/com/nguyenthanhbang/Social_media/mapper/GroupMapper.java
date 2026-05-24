package com.nguyenthanhbang.Social_media.mapper;

import com.nguyenthanhbang.Social_media.dto.response.GroupResponse;
import com.nguyenthanhbang.Social_media.model.Group;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupMapper {
    GroupResponse toGroupResponse(Group group);
    List<GroupResponse> toGroupResponses(List<Group> groups);
}
