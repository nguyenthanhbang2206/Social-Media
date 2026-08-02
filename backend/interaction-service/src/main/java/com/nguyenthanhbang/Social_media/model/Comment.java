package com.nguyenthanhbang.Social_media.model;

import com.nguyenthanhbang.Social_media.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;


@Entity
@Table(name = "comments")
@Where(clause = "active = true")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "parent_comment_id")
    private Long parentCommentId;
}
