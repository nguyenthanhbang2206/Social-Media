package com.nguyenthanhbang.Social_media.model;

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
public class PostShare extends BaseEntity {
    @Column(columnDefinition = "TEXT")
    private String shareContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
