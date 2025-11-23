package com.nguyenthanhbang.Social_media.repository;

import com.nguyenthanhbang.Social_media.model.GroupPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupPostRepository extends JpaRepository<GroupPost, Long> {
    List<GroupPost> findByGroupIdAndIsApproved(Long groupId, Boolean isApproved);
}
