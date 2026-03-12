package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record CommentResponse(
        Long commentId,
        String profileImageUrl,
        String author,
        Long authorId,
        Long postId,
        String content) {}
