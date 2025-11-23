package com.nguyenthanhbang.Social_media.repository;

import com.nguyenthanhbang.Social_media.enumeration.GroupMembershipStatus;
import com.nguyenthanhbang.Social_media.enumeration.GroupRole;
import com.nguyenthanhbang.Social_media.model.Group;
import com.nguyenthanhbang.Social_media.model.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    @Query("SELECT gm.group FROM GroupMember gm WHERE gm.user.id = :userId and gm.status = 'APPROVED'")
    List<Group> myGroups(@Param("userId") Long userId);
    GroupMember findByUserId(Long userId);
    boolean existsByGroupIdAndUserIdAndStatus(Long groupId, Long userId, GroupMembershipStatus status);
    boolean existsByGroupIdAndUserIdAndRole(Long groupId, Long userId, GroupRole role);
    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);
    Optional<GroupMember> findByGroupIdAndUserIdAndRoleIn(Long groupId, Long userId, List<GroupRole> roles);
    List<GroupMember> findByGroupIdAndRoleIn(Long groupId, List<GroupRole> roles);
    List<GroupMember> findByGroupIdAndStatus(Long groupId, GroupMembershipStatus status);
}
