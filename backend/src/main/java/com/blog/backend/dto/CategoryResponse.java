package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record CategoryResponse(
        String categoryName
) {
}
