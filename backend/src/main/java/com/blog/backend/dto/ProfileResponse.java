package com.blog.backend.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProfileResponse(
        Long id,
        String username,
        String email,
        String bio,
        String profileImageUrl,
        LocalDateTime createdAt,
        Long followerCount,
        Long followingCount,
        Long postCount,
        boolean isFollowing
) {
}
