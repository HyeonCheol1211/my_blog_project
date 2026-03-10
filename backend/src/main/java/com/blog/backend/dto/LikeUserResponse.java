package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record LikeUserResponse(
        Long userId,
        String username,
        String profileImageUrl
) {
}
