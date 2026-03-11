package com.blog.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String profileImage = "/images/profiles/basic_profile_image.png";

    private String bio;

    @CreationTimestamp
    private LocalDateTime createdAt;


    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateBio(String bio) {
        this.bio = bio;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public boolean canDeleteImage() {
       return !profileImage.equals("/images/profiles/basic_profile_image.png");
    }
}