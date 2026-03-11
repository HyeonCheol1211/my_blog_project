package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record FollowerResponse(
        Long followerId,
        String username,
        String profileImageUrl
) {
}
