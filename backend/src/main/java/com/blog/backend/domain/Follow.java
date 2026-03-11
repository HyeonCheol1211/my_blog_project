package com.blog.backend.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "follows")
@NoArgsConstructor
@Getter
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User following;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Follow(User follower, User following){
        this.follower = follower;
        this.following = following;
    }

    public Long getFollowerId() {
        return follower.getId();
    }

    public String getFollowerProfileImage() {
        return follower.getProfileImage();
    }

    public String getFollowerUsername() {
        return follower.getUsername();
    }

    public Long getFollowingId() {
        return following.getId();
    }

    public String getFollowingProfileImage() {
        return following.getProfileImage();
    }

    public String getFollowingUsername() {
        return following.getUsername();
    }
}
