package com.nguyenthanhbang.Social_media.model;

import com.nguyenthanhbang.Social_media.common.enumeration.GroupMembershipStatus;
import com.nguyenthanhbang.Social_media.common.enumeration.GroupRole;
import com.nguyenthanhbang.Social_media.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "active = true")
public class GroupMember extends BaseEntity{
    @Enumerated(EnumType.STRING)
    private GroupRole role = GroupRole.MEMBER;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;


    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GroupMembershipStatus status;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved = false;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

}

