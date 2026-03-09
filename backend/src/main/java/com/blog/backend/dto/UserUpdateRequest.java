package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record UserUpdateRequest(
        String password,
        String email,
        String bio
){
}
