package com.nguyenthanhbang.Social_media.model;

import com.nguyenthanhbang.Social_media.enumeration.ReactionType;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
