package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record AppVersionResponse(
        String appName, String version, String commitSha, String builtAt, String deployedAt) {}
