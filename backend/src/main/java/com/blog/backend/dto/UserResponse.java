package com.blog.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserResponse(
        @NotBlank
        String username,
        String email
) {
}
