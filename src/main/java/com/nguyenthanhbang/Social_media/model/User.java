package com.nguyenthanhbang.Social_media.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nguyenthanhbang.Social_media.enumeration.Gender;
import com.nguyenthanhbang.Social_media.enumeration.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET active = false WHERE id = ?")
@Where(clause = "active = true")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseModel{
    @Column(unique=true, nullable=false)
    private String email;
    @JsonIgnore
    private String password;
    private String fullName;
    private String avatar;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;
    @Enumerated(value = EnumType.STRING)
    private Gender gender;
    @Enumerated(value = EnumType.STRING)
    private Role role;
    private LocalDate dateOfBirth;
    private String coverPhoto;

    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendShip> sentFriendRequests = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendShip> receivedFriendRequests = new ArrayList<>();
}
