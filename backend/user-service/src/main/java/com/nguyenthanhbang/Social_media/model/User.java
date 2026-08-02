package com.nguyenthanhbang.Social_media.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nguyenthanhbang.Social_media.common.enumeration.Gender;
import com.nguyenthanhbang.Social_media.common.enumeration.Role;
import com.nguyenthanhbang.Social_media.common.model.BaseEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    @Column(unique=true, nullable=false)
    private String email;
    @JsonIgnore
    @Nullable
    private String password;
    private String fullName;
    private String avatar;
    @Enumerated(value = EnumType.STRING)
    private Gender gender;
    @Enumerated(value = EnumType.STRING)
    private Role role;
    private LocalDate dateOfBirth;
    private String coverPhoto;

    @Column(name = "keycloak_id", unique = true)
    private UUID keycloakId;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    @Builder.Default
    private boolean accountLocked = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean emailVerified = false;
}
