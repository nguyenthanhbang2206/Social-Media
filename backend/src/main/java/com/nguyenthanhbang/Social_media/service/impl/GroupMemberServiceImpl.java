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
        if(groupMemberRepository.existsByGroupIdAndUserIdAndStatus(groupId, user.getId(), GroupMembershipStatus.APPROVED)) {
            throw new IllegalStateException("User already joined the group");
        }
        if(groupMemberRepository.existsByGroupIdAndUserIdAndStatus(groupId, user.getId(), GroupMembershipStatus.PENDING)) {
            throw new IllegalStateException("Wait for accepting by admin");
        }
        GroupMembershipStatus status = GroupMembershipStatus.PENDING;
        Boolean isApproved = false;
        if(group.getPrivacy().equals(GroupPrivacy.PUBLIC)){
            status = GroupMembershipStatus.APPROVED;
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
        if(groupMember.getStatus() != GroupMembershipStatus.APPROVED){
            throw new RuntimeException("Group member is not in this group");
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
        if(groupMember.getStatus() != GroupMembershipStatus.PENDING){
            throw new IllegalStateException("Group member is not in pending status");
        }
        groupMember.setJoinedAt(LocalDateTime.now());
        groupMember.setIsApproved(true);
        groupMember.setStatus(GroupMembershipStatus.APPROVED);
        groupMemberRepository.save(groupMember);
    }

    @Override
    public void rejectMember(Long groupId, Long userId) {
        User user = userService.getUserLogin();
        if(!this.isAdmin(groupId, user.getId())) {
            throw new AccessDeniedException("You do not have permission to reject this member");
        }
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId).orElseThrow(()-> new EntityNotFoundException("Member not found"));
        if(groupMember.getStatus() != GroupMembershipStatus.PENDING){
            throw new IllegalStateException("Group member is not in pending status");
        }
        groupMember.setRejectedAt(LocalDateTime.now());
        groupMember.setIsApproved(false);
        groupMember.setStatus(GroupMembershipStatus.REJECTED);
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
        if(groupMember.getStatus() != GroupMembershipStatus.APPROVED){
            throw new RuntimeException("Group member is not in this group");
        }
        group.getMembers().remove(groupMember);
        groupMemberRepository.delete(groupMember);
    }

    @Override
    public GroupMember changeRole(Long groupId, Long userId, GroupRole role) {
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserIdAndRoleIn(groupId, userId, Arrays.asList(GroupRole.ADMIN, GroupRole.MEMBER)).orElseThrow(()-> new EntityNotFoundException("Group member not found"));
        User user = userService.getUserLogin();
        Group group = groupService.getGroupById(groupId);
        if(!group.getCreator().getId().equals(user.getId())){
            throw new IllegalStateException("You are not owner of this group");
        }
        groupMember.setRole(role);
        groupMemberRepository.save(groupMember);
        return groupMember;
    }

    @Override
    public List<GroupMember> getMembers(Long groupId) {
        Group group = groupService.getGroupById(groupId);
        return groupMemberRepository.findByGroupIdAndStatus(groupId, GroupMembershipStatus.APPROVED);
    }

    @Override
    public List<GroupMember> getPendingMembers(Long groupId) {
        User user = userService.getUserLogin();
        if(!this.isAdmin(groupId, user.getId())) {
            throw new IllegalStateException("You can not view pending member");
        }
        List<GroupMember> pendingMembers = groupMemberRepository.findByGroupIdAndStatus(groupId, GroupMembershipStatus.PENDING);
        return pendingMembers;
    }

    @Override
    public GroupMembershipStatus getMembershipStatus(Long groupId) {
        User user  = userService.getUserLogin();
        Group group = groupService.getGroupById(groupId);
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, user.getId()).orElseThrow(()-> new EntityNotFoundException("Group member not found"));
        return groupMember.getStatus();
    }


    private boolean isAdmin(Long groupId, Long userId) {
        return groupMemberRepository.existsByGroupIdAndUserIdAndRole(groupId, userId, GroupRole.ADMIN);
    }
}
