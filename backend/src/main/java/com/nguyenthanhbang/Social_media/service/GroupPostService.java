package com.nguyenthanhbang.Social_media.service;

import com.nguyenthanhbang.Social_media.dto.request.GroupPostRequest;
import com.nguyenthanhbang.Social_media.model.GroupPost;

import java.util.List;

public interface GroupPostService {
    GroupPost createGroupPost(Long groupId, GroupPostRequest request);
    GroupPost updateGroupPost(Long groupId, GroupPostRequest request);
    GroupPost approve(Long groupId, Long postId);
    void deleteGroupPost(Long groupId, Long postId);
    List<GroupPost> getAllGroupPosts(Long groupId);
}
