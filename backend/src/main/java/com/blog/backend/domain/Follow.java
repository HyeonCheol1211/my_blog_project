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
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user2;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Follow(User user1, User user2){
        this.user1 = user1;
        this.user2 = user2;
    }

}
