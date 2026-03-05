package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record FollowResponse(
        String username1,
        String username2
) {
}
