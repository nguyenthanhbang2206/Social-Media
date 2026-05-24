package com.nguyenthanhbang.Social_media.model;

import com.nguyenthanhbang.Social_media.common.enumeration.ReactionType;
import com.nguyenthanhbang.Social_media.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "active = true")
public class PostLike extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private ReactionType reactionType = ReactionType.LIKE;


    @Column(name = "user_id", nullable = false)
    private Long userId;


    @Column(name = "post_id", nullable = false)
    private Long postId;
}
