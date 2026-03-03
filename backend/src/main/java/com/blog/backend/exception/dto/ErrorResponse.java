package com.blog.backend.exception.dto;

import lombok.Builder;

@Builder
public record ErrorResponse(
        int status,
        String error,
        String message
) {
}
