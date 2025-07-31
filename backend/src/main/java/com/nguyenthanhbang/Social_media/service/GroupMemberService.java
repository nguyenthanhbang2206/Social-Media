package com.nguyenthanhbang.Social_media.service;

import com.nguyenthanhbang.Social_media.enumeration.GroupRole;
import com.nguyenthanhbang.Social_media.model.GroupMember;

import java.util.List;

public interface GroupMemberService {
    void joinGroup(Long groupId);
    void leaveGroup(Long groupId);
    void approveMember(Long groupId, Long userId);
    void deleteMember(Long groupId, Long userId);
    GroupMember changeRole(Long groupId, Long userId, GroupRole role);
    List<GroupMember> getMembers(Long groupId);
}
