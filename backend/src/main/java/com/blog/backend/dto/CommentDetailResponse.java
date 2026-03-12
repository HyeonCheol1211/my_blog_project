package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record CommentDetailResponse(
        Long commentId,
        String author,
        String profileImageUrl,
        Long postId,
        String postTitle,
        String content) {}
