package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.enumeration.GroupMembershipStatus;
import com.nguyenthanhbang.Social_media.enumeration.GroupPrivacy;
import com.nguyenthanhbang.Social_media.enumeration.GroupRole;
import com.nguyenthanhbang.Social_media.model.Group;
import com.nguyenthanhbang.Social_media.model.GroupMember;
import com.nguyenthanhbang.Social_media.model.User;
import com.nguyenthanhbang.Social_media.repository.GroupMemberRepository;
import com.nguyenthanhbang.Social_media.service.GroupMemberService;
import com.nguyenthanhbang.Social_media.service.GroupService;
import com.nguyenthanhbang.Social_media.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupMemberServiceImpl implements GroupMemberService {
    private final GroupMemberRepository groupMemberRepository;
    private final GroupService groupService;
    private final UserService userService;
    @Override
    public void joinGroup(Long groupId) {
        User user = userService.getUserLogin();
        Group group = groupService.getGroupById(groupId);
        if(groupMemberRepository.existsByGroupIdAndUserId(groupId, user.getId())) {
            throw new IllegalStateException("User already joined the group");
        }
        GroupMembershipStatus status = GroupMembershipStatus.PENDING;
        Boolean isApproved = false;
        if(group.getPrivacy().equals(GroupPrivacy.PUBLIC)){
            status = GroupMembershipStatus.MEMBER;
            isApproved = true;
        }
        GroupMember groupMember = GroupMember.builder()
                .group(group)
                .user(user)
                .role(GroupRole.MEMBER)
                .requestedAt(LocalDateTime.now())
                .status(status)
                .joinedAt(isApproved ? LocalDateTime.now() : null)
                .isApproved(isApproved)
                .build();
        groupMemberRepository.save(groupMember);
    }

    @Override
    public void leaveGroup(Long groupId) {
        Group group = groupService.getGroupById(groupId);
        User user = userService.getUserLogin();
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, user.getId()).orElseThrow(()->new EntityNotFoundException("Group member not found"));
        if(group.getCreator().getId().equals(user.getId())){
            throw new RuntimeException("Admin can not leave group");
        }
        group.getMembers().remove(groupMember);
        groupMemberRepository.delete(groupMember);
    }

    @Override
    public void approveMember(Long groupId, Long userId) {
        User user = userService.getUserLogin();
        if(!this.isAdmin(groupId, user.getId())) {
            throw new AccessDeniedException("You do not have permission to approve this member");
        }
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId).orElseThrow(()-> new EntityNotFoundException("Member not found"));
        groupMember.setJoinedAt(LocalDateTime.now());
        groupMember.setIsApproved(true);
        groupMember.setStatus(GroupMembershipStatus.MEMBER);
        groupMemberRepository.save(groupMember);
    }

    @Override
    public void deleteMember(Long groupId, Long userId) {
        Group group = groupService.getGroupById(groupId);
        User user = userService.getUserById(userId);
        User currentUser = userService.getUserLogin();
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId).orElseThrow(()->new EntityNotFoundException("Group member not found"));
        if(!this.isAdmin(groupId, currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to delete this member");
        }
        if(group.getCreator().getId().equals(user.getId())){
            throw new RuntimeException("Can not delete admin");
        }
        group.getMembers().remove(groupMember);
        groupMemberRepository.delete(groupMember);
    }

    @Override
    public GroupMember changeRole(Long groupId, Long userId, GroupRole role) {
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserIdAndStatusIn(groupId, userId, Arrays.asList(GroupMembershipStatus.ADMIN, GroupMembershipStatus.MEMBER)).orElseThrow(()-> new EntityNotFoundException("Group member not found"));
        User user = userService.getUserLogin();
        groupMember.setRole(role);
        groupMemberRepository.save(groupMember);
        return groupMember;
    }

    @Override
    public List<GroupMember> getMembers(Long groupId) {
        Group group = groupService.getGroupById(groupId);
        return groupMemberRepository.findByGroupIdAndStatusIn(groupId, Arrays.asList(GroupMembershipStatus.MEMBER, GroupMembershipStatus.ADMIN ));
    }

    private boolean isAdmin(Long groupId, Long userId) {
        return groupMemberRepository.existsByGroupIdAndUserIdAndRole(groupId, userId, GroupRole.ADMIN);
    }
}
