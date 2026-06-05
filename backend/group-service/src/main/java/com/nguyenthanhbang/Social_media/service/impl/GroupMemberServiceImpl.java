package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.client.UserClient;
import com.nguyenthanhbang.Social_media.common.dto.UserSummaryResponse;
import com.nguyenthanhbang.Social_media.common.enumeration.GroupMembershipStatus;
import com.nguyenthanhbang.Social_media.common.enumeration.GroupPrivacy;
import com.nguyenthanhbang.Social_media.common.enumeration.GroupRole;
import com.nguyenthanhbang.Social_media.common.event.GroupEvent;
import com.nguyenthanhbang.Social_media.common.util.RequestHeaderUtil;
import com.nguyenthanhbang.Social_media.event.GroupPublisher;
import com.nguyenthanhbang.Social_media.model.Group;
import com.nguyenthanhbang.Social_media.model.GroupMember;
import com.nguyenthanhbang.Social_media.repository.GroupMemberRepository;
import com.nguyenthanhbang.Social_media.service.GroupMemberService;
import com.nguyenthanhbang.Social_media.service.GroupService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupMemberServiceImpl implements GroupMemberService {
    private final GroupMemberRepository groupMemberRepository;
    private final GroupService groupService;
    private final GroupPublisher groupPublisher;
    private final UserClient userClient;

    @Override
    public void joinGroup(Long groupId) {
        Long userId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        UserSummaryResponse userSummaryResponse = userClient.getUserById(userId).getData();

        Group group = groupService.getGroupById(groupId);
        Optional<GroupMember> optionalGroupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if(optionalGroupMember.isPresent()) {
            GroupMember existingGroupMember = optionalGroupMember.get();
            if(existingGroupMember.getStatus() != null){
                if(existingGroupMember.getStatus() == GroupMembershipStatus.APPROVED){
                    throw new IllegalStateException("User already joined the group");
                }else if(existingGroupMember.getStatus() == GroupMembershipStatus.PENDING){
                    throw new IllegalStateException("Wait for accepting by admin");
                }
                existingGroupMember.setStatus(GroupMembershipStatus.PENDING);
                existingGroupMember.setRequestedAt(LocalDateTime.now());
                existingGroupMember.setRole(GroupRole.MEMBER);
                existingGroupMember.setJoinedAt(null);
                existingGroupMember.setIsApproved(false);
                groupMemberRepository.save(existingGroupMember);


            }
        }
        else {
            GroupMembershipStatus status = GroupMembershipStatus.PENDING;
            Boolean isApproved = false;
            if(group.getPrivacy().equals(GroupPrivacy.PUBLIC)){
                status = GroupMembershipStatus.APPROVED;
                isApproved = true;

            }

            GroupMember groupMember = GroupMember.builder()
                .groupId(groupId)
                .userId(userId)
                .role(GroupRole.MEMBER)
                .requestedAt(LocalDateTime.now())
                .status(status)
                .joinedAt(isApproved ? LocalDateTime.now() : null)
                .isApproved(isApproved)
                .build();
            groupMemberRepository.save(groupMember);
        }

        if(group.getPrivacy() == GroupPrivacy.PRIVATE){
            GroupEvent groupEvent = GroupEvent
                    .builder()
                    .recipientId(group.getCreatorId())
                    .groupImage(group.getGroupImage())
                    .eventType(GroupEvent.GroupEventType.REQUESTED)
                    .privacy(group.getPrivacy())
                    .name(group.getName())
                    .actorName(userSummaryResponse.getFullName())
                    .ownerId(group.getCreatorId())
                    .actorId(userId)
                    .build();
            log.info("-----------------publish group event , type = {} ,---------", groupEvent.getEventType());
            groupPublisher.publishGroupEvent(groupEvent, "group.requested");
        }

    }

    @Override
    public void leaveGroup(Long groupId) {
        Group group = groupService.getGroupById(groupId);
        Long userId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId).orElseThrow(()->new EntityNotFoundException("Group member not found"));
        if(group.getCreatorId().equals(userId)){
            throw new RuntimeException("Admin can not leave group");
        }
        if(groupMember.getStatus() != GroupMembershipStatus.APPROVED){
            throw new RuntimeException("Group member is not in this group");
        }
        groupMemberRepository.delete(groupMember);
    }

    @Override
    public void approveMember(Long groupId, Long userId) {
        Long currentUserId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        Group group = groupService.getGroupById(groupId);

        if(!this.isAdmin(groupId, currentUserId)) {
            throw new RuntimeException("You do not have permission to approve this member");
        }
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId).orElseThrow(()-> new EntityNotFoundException("Member not found"));
        if(groupMember.getStatus() != GroupMembershipStatus.PENDING){
            throw new IllegalStateException("Group member is not in pending status");
        }
        groupMember.setJoinedAt(LocalDateTime.now());
        groupMember.setIsApproved(true);
        groupMember.setStatus(GroupMembershipStatus.APPROVED);
        groupMemberRepository.save(groupMember);
        //        approved
        GroupEvent groupEvent = GroupEvent
                .builder()
                .recipientId(groupMember.getUserId())
                .actorId(group.getCreatorId())
                .groupImage(group.getGroupImage())
                .eventType(GroupEvent.GroupEventType.APPROVED)
                .privacy(group.getPrivacy())
                .name(group.getName())
                .ownerId(group.getCreatorId())
                .build();
        log.info("-----------------publish group event , type = {} ,---------", groupEvent.getEventType());
        groupPublisher.publishGroupEvent(groupEvent, "group.approved");
    }

    @Override
    public void rejectMember(Long groupId, Long userId) {
        Long currentUserId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        if(!this.isAdmin(groupId, currentUserId)) {
            throw new RuntimeException("You do not have permission to reject this member");
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
        Long currentUserId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId).orElseThrow(()->new EntityNotFoundException("Group member not found"));
        if(!this.isAdmin(groupId, currentUserId)) {
            throw new RuntimeException("You do not have permission to delete this member");
        }
        if(group.getCreatorId().equals(userId)){
            throw new RuntimeException("Can not delete admin");
        }
        if(groupMember.getStatus() != GroupMembershipStatus.APPROVED){
            throw new RuntimeException("Group member is not in this group");
        }
        groupMemberRepository.delete(groupMember);
    }

    @Override
    public GroupMember changeRole(Long groupId, Long userId, GroupRole role) {
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserIdAndRoleIn(groupId, userId, Arrays.asList(GroupRole.ADMIN, GroupRole.MEMBER)).orElseThrow(()-> new EntityNotFoundException("Group member not found"));
        Long currentUserId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        Group group = groupService.getGroupById(groupId);
        if(!group.getCreatorId().equals(currentUserId)){
            throw new IllegalStateException("You are not owner of this group");
        }
        groupMember.setRole(role);
        groupMemberRepository.save(groupMember);
        return groupMember;
    }

    @Override
    public List<GroupMember> getMembers(Long groupId) {
        return groupMemberRepository.findByGroupIdAndStatus(groupId, GroupMembershipStatus.APPROVED);
    }

    @Override
    public List<GroupMember> getPendingMembers(Long groupId) {
        Long userId = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        if(!this.isAdmin(groupId, userId)) {
            throw new IllegalStateException("You can not view pending member");
        }
        List<GroupMember> pendingMembers = groupMemberRepository.findByGroupIdAndStatus(groupId, GroupMembershipStatus.PENDING);
        return pendingMembers;
    }

    @Override
    public GroupMembershipStatus getMembershipStatus(Long groupId) {
        Long userId  = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        groupService.getGroupById(groupId);
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId).orElseThrow(()-> new EntityNotFoundException("Group member not found"));
        return groupMember.getStatus();
    }

    @Override
    public boolean isCurrentUserAdmin(Long groupId) {
        Long userId  = RequestHeaderUtil.getUserId().orElseThrow(() -> new EntityNotFoundException("User not found"));
        return isAdmin(groupId, userId);
    }


    private boolean isAdmin(Long groupId, Long userId) {
        return groupMemberRepository.existsByGroupIdAndUserIdAndRole(groupId, userId, GroupRole.ADMIN);
    }
}
