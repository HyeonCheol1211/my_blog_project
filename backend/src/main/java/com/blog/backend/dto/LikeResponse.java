package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record LikeResponse(Long userId, Long postId) {}
