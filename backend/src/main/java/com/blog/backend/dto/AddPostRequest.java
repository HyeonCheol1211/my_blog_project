package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record AddPostRequest(
        String categoryName, String title, String content, boolean publicStatus) {}
