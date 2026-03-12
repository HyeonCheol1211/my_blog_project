package com.blog.backend.dto;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record PostResponse(
        Long id,
        String title,
        String content,
        Long authorId,
        String author,
        boolean publicStatus,
        LocalDateTime createdAt,
        Long likeCount,
        String profileImageUrl) {}
