package com.blog.backend.dto;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record ProfileBasicResponse(
        Long id,
        String username,
        String email,
        String bio,
        String profileImageUrl,
        LocalDateTime createdAt,
        Long followerCount,
        Long followingCount) {}
