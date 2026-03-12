package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record DeletePostResponse(Long id, String message) {}
