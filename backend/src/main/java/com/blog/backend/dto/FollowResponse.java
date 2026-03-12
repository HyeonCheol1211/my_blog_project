package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record FollowResponse(String followerUsername, String followingUsername) {}
