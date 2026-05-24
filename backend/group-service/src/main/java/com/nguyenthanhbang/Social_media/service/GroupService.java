package com.nguyenthanhbang.Social_media.service;

import com.nguyenthanhbang.Social_media.dto.request.GroupRequest;
import com.nguyenthanhbang.Social_media.model.Group;

import java.util.List;

public interface GroupService {
    Group createGroup(GroupRequest request);
    List<Group> getAllGroups();
    Group getGroupById(Long id);
    Group updateGroup(Long id, GroupRequest request);
    void deleteGroup(Long id);
    List<Group> searchGroup(String keyword);
    List<Group> myGroups();
}
