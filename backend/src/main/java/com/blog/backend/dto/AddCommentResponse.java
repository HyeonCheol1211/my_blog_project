package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record AddCommentResponse(
        String author,
        String content,
        Long postId
) {
}
