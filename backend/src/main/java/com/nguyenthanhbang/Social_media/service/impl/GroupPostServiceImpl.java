package com.nguyenthanhbang.Social_media.service.impl;

import com.nguyenthanhbang.Social_media.dto.request.GroupPostRequest;
import com.nguyenthanhbang.Social_media.enumeration.GroupMembershipStatus;
import com.nguyenthanhbang.Social_media.enumeration.GroupPrivacy;
import com.nguyenthanhbang.Social_media.enumeration.GroupRole;
import com.nguyenthanhbang.Social_media.model.Group;
import com.nguyenthanhbang.Social_media.model.GroupMember;
import com.nguyenthanhbang.Social_media.model.GroupPost;
import com.nguyenthanhbang.Social_media.model.User;
import com.nguyenthanhbang.Social_media.repository.GroupMemberRepository;
import com.nguyenthanhbang.Social_media.repository.GroupPostRepository;
import com.nguyenthanhbang.Social_media.service.GroupPostService;
import com.nguyenthanhbang.Social_media.service.GroupService;
import com.nguyenthanhbang.Social_media.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupPostServiceImpl implements GroupPostService {
    private final GroupService groupService;
    private final UserService userService;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupPostRepository groupPostRepository;

    @Override
    public GroupPost createGroupPost(Long groupId, GroupPostRequest request) {
        Group group = groupService.getGroupById(groupId);
        User user = userService.getUserLogin();
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserIdAndRoleIn(groupId, user.getId(), Arrays.asList(GroupRole.MEMBER, GroupRole.ADMIN)).orElseThrow(()-> new EntityNotFoundException("Member not found"));
        Boolean isApproved = true;
        if(group.getPrivacy().equals(GroupPrivacy.PRIVATE)) {
            isApproved = false;
        }
        GroupPost post = GroupPost.builder()
                .content(request.getContent())
                .group(group)
                .user(user)
                .isApproved(isApproved)
                .build();
        return groupPostRepository.save(post);
    }

    @Override
    public GroupPost updateGroupPost(Long groupId, GroupPostRequest request) {
        return null;
    }

    @Override
    public GroupPost approve(Long groupId, Long postId) {
        GroupPost post = groupPostRepository.findById(postId).orElseThrow(()-> new EntityNotFoundException("Post not found"));
        if(post.getGroup() == null || !post.getGroup().getId().equals(groupId)) {
            throw new EntityNotFoundException("Post is not belong to group");
        }
        post.setIsApproved(true);
        return groupPostRepository.save(post);
    }

    @Override
    public void deleteGroupPost(Long groupId, Long postId) {

    }

    @Override
    public List<GroupPost> getAllGroupPosts(Long groupId) {
        return groupPostRepository.findByGroupIdAndIsApproved(groupId, true);
    }
}
