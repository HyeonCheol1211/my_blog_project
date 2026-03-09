package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record LikeUserResponse(
        String username,
        String profileImageUrl
) {
}
