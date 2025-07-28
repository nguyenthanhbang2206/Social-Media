package com.nguyenthanhbang.Social_media.model;

import com.nguyenthanhbang.Social_media.enumeration.GroupRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;
//group 1:n group user n:1 user
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


    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;
}

