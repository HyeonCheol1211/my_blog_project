package com.blog.backend.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
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

    private String profileImage;
    private String bio;

    @CreationTimestamp
    private LocalDateTime createdAt;


    @Builder
    public User(String username, String email, String password, String profileImage, String bio) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
        this.bio = bio;
    }

}