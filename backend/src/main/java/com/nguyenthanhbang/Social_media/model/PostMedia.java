package com.nguyenthanhbang.Social_media.model;

import com.nguyenthanhbang.Social_media.enumeration.MediaType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Where(clause = "active = true")
public class PostMedia extends BaseEntity {

        @Column(name = "media_url", nullable = false)
        private String mediaUrl;

        @Column(name = "media_type")
        @Enumerated(EnumType.STRING)
        private MediaType mediaType;

        @Column(name = "upload_order")
        private Integer uploadOrder;

        // Relationships
        @ManyToOne(fetch = FetchType.LAZY)
       @JoinColumn(name = "post_id")
        private Post post;

}
