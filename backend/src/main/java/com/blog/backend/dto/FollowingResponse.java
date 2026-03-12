package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record FollowingResponse(Long followingId, String username, String profileImageUrl) {}
