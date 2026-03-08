package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record FollowingResponse(
        String username,
        String profileImageUrl
) {
}
