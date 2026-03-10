package com.blog.backend.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostDetailResponse(
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
        boolean liked,
        String profileImageUrl) {
}
