package com.blog.backend.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(
        name = "follows",
        uniqueConstraints =
                @UniqueConstraint(
                        name = "uk_follow_follower_following",
                        columnNames = {"follower_id", "following_id"}))
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

    @CreationTimestamp private LocalDateTime createdAt;

    @Builder
    public Follow(User follower, User following) {
        this.follower = follower;
        this.following = following;
    }

    public Long getFollowerId() {
        return follower.getId();
    }

    public String getFollowerProfileImage() {
        return follower.getProfileImageUrl();
    }

    public String getFollowerUsername() {
        return follower.getUsername();
    }

    public Long getFollowingId() {
        return following.getId();
    }

    public String getFollowingProfileImage() {
        return following.getProfileImageUrl();
    }

    public String getFollowingUsername() {
        return following.getUsername();
    }
}
