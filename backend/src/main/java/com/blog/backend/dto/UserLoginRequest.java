package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record UserLoginRequest(String username, String password) {}
