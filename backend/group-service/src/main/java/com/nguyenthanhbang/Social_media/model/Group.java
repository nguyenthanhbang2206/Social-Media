package com.nguyenthanhbang.Social_media.model;

import com.nguyenthanhbang.Social_media.common.enumeration.GroupPrivacy;
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
@Table(name = "groups")
@Where(clause = "active = true")
public class Group extends BaseEntity{
    private String name;
    private String description;
    private String groupImage;
    private String coverImage;
    private GroupPrivacy privacy = GroupPrivacy.PUBLIC;


    @Column(name = "creator_id", nullable = false)
    private Long creatorId;
}
