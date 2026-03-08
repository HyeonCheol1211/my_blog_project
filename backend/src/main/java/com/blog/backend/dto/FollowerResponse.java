package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record FollowerResponse(
        String username,
        String profileImageUrl
) {
}
