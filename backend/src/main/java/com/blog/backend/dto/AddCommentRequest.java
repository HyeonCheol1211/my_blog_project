package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record AddCommentRequest(String content) {}
