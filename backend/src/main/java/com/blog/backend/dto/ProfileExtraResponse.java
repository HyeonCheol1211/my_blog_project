package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record ProfileExtraResponse(
        Long postAllCount,
        Long postPublicCount,
        boolean isFollowing
) {
}
