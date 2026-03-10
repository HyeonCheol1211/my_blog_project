package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record LoginResponse (
    Long userId,
    String token
){
}
