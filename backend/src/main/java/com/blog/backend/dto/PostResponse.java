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
        String categoryName,
        boolean publicStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long likeCount,
        String profileImageUrl) {}
