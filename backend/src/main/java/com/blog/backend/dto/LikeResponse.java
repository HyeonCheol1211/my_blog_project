package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record LikeResponse(
        String username,
        Long postId
) {
}
