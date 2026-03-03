package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record UpdatePostRequest (
    String categoryName,
    String title,
    String content,
    boolean publicStatus)
{
}
