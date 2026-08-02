package com.nguyenthanhbang.Social_media.model;

import com.nguyenthanhbang.Social_media.common.enumeration.PostType;
import com.nguyenthanhbang.Social_media.common.enumeration.PrivacyLevel;
import com.nguyenthanhbang.Social_media.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Where(clause = "active = true")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {
    @Column(columnDefinition = "TEXT")
    private String content;
    @Enumerated(EnumType.STRING)
    private PrivacyLevel privacy = PrivacyLevel.PUBLIC;
    @Column(name = "total_reactions")
    private Long totalReactions = 0L;
    @Column(name = "total_comments")
    private Long totalComments = 0L;
    @Column(name = "total_shares")
    private Long totalShares = 0L;
    // Group fields
    @Column(name = "is_approved")
    private Boolean isApproved = true;  // Cho group private

    @Column(name = "is_pinned")
    private Boolean isPinned = false;  // Pin post trong group
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "group_id")
    private Long groupId;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false)
    private PostType postType = PostType.USER_POST;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostMedia> media = new ArrayList<>();
}
