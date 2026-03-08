package com.blog.backend.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostDetailResponse (
    Long id,
    String title,
    String content,
    String author,
    String categoryName,
    boolean publicStatus,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long likeCount,
    List<CommentResponse> commentsResponse,
    boolean liked,
    String profileImageUrl
)
    {
}
