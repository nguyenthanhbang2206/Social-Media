package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.dto.request.GroupRequest;
import com.nguyenthanhbang.Social_media.enumeration.GroupMembershipStatus;
import com.nguyenthanhbang.Social_media.enumeration.GroupRole;
import com.nguyenthanhbang.Social_media.model.Group;
import com.nguyenthanhbang.Social_media.model.GroupMember;
import com.nguyenthanhbang.Social_media.model.User;
import com.nguyenthanhbang.Social_media.repository.GroupMemberRepository;
import com.nguyenthanhbang.Social_media.repository.GroupRepository;
import com.nguyenthanhbang.Social_media.service.GroupService;
import com.nguyenthanhbang.Social_media.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final UserService userService;
    private final GroupMemberRepository groupMemberRepository;

    @Override
    public Group createGroup(GroupRequest request) {
        User user = userService.getUserLogin();
        Group newGroup = new Group();
        newGroup.setName(request.getName());
        newGroup.setDescription(request.getDescription());
        newGroup.setGroupImage(request.getGroupImage());
        newGroup.setCoverImage(request.getCoverImage());
        newGroup.setPrivacy(request.getPrivacy());
        newGroup.setCreator(user);
        GroupMember groupMember = GroupMember.builder()
                .user(user)
                .group(newGroup)
                .role(GroupRole.ADMIN)
                .joinedAt(LocalDateTime.now())
                .status(GroupMembershipStatus.ADMIN)
                .isApproved(true)
                .build();
        newGroup.getMembers().add(groupMember);
        return groupRepository.save(newGroup);
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
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setCoverImage(request.getCoverImage());
        group.setGroupImage(group.getCoverImage());
        group.setPrivacy(request.getPrivacy());
        return groupRepository.save(group);
    }

    @Override
    public void deleteGroup(Long id) {
        Group group = getGroupById(id);
        group.setActive(false);
    }

    @Override
    public List<Group> searchGroup(String keyword) {
        return groupRepository.search(keyword);
    }

    @Override
    public List<Group> myGroups() {
        User user = userService.getUserLogin();
        return groupMemberRepository.myGroups(user.getId());
    }
}
