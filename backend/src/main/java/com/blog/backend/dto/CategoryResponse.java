package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record CategoryResponse(Long id, String categoryName, Long postsCount) {}
