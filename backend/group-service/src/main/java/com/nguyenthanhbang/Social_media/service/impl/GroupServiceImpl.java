package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.client.UserClient;
import com.nguyenthanhbang.Social_media.common.dto.ApiResponse;
import com.nguyenthanhbang.Social_media.dto.request.GroupRequest;
import com.nguyenthanhbang.Social_media.common.enumeration.GroupMembershipStatus;
import com.nguyenthanhbang.Social_media.common.enumeration.GroupRole;
import com.nguyenthanhbang.Social_media.common.util.RequestHeaderUtil;
import com.nguyenthanhbang.Social_media.common.dto.UserSummaryResponse;
import com.nguyenthanhbang.Social_media.model.Group;
import com.nguyenthanhbang.Social_media.model.GroupMember;
import com.nguyenthanhbang.Social_media.repository.GroupMemberRepository;
import com.nguyenthanhbang.Social_media.repository.GroupRepository;
import com.nguyenthanhbang.Social_media.service.GroupService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserClient userClient;

    @Override
    public Group createGroup(GroupRequest request) {
        Long userId = getCurrentUserId();
        Group newGroup = new Group();
        newGroup.setName(request.getName());
        newGroup.setDescription(request.getDescription());
        newGroup.setGroupImage(request.getGroupImage());
        newGroup.setCoverImage(request.getCoverImage());
        newGroup.setPrivacy(request.getPrivacy());
        newGroup.setCreatorId(userId);
        GroupMember groupMember = GroupMember.builder()
            .userId(userId)
            .groupId(null)
                .role(GroupRole.ADMIN)
                .joinedAt(LocalDateTime.now())
                .status(GroupMembershipStatus.APPROVED)
                .isApproved(true)
                .build();
        Group savedGroup = groupRepository.save(newGroup);
        groupMember.setGroupId(savedGroup.getId());
        groupMemberRepository.save(groupMember);
        return savedGroup;
    }

    @Override
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    @Override
    public Group getGroupById(Long id) {
        return groupRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Group not found"));
    }

    @Override
    public Group updateGroup(Long id, GroupRequest request) {
        Group group = getGroupById(id);
        Long userId = getCurrentUserId();
        if(!group.getCreatorId().equals(userId)) {
            throw new IllegalStateException("You can not update this group");
        }
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setCoverImage(request.getCoverImage());
        group.setGroupImage(request.getGroupImage());
        group.setPrivacy(request.getPrivacy());
        return groupRepository.save(group);
    }

    @Override
    public void deleteGroup(Long id) {
        Group group = getGroupById(id);
        group.setActive(false);
        groupRepository.save(group);
    }

    @Override
    public List<Group> searchGroup(String keyword) {
        return groupRepository.search(keyword);
    }

    @Override
    public List<Group> myGroups() {
        Long userId = getCurrentUserId();
        List<Long> groupIds = groupMemberRepository.myGroupIds(userId);
        return groupRepository.findAllById(groupIds);
    }

    private Long getCurrentUserId() {
        String email = RequestHeaderUtil.getUserEmail()
                .orElseThrow(() -> new EntityNotFoundException("User not found - X-User-Email header missing"));
        
        ApiResponse<UserSummaryResponse> response = userClient.getUserByEmail(email);
        if (response == null || response.getData() == null) {
            throw new EntityNotFoundException("User not found with email: " + email);
        }
        
        return response.getData().getId();
    }
}
