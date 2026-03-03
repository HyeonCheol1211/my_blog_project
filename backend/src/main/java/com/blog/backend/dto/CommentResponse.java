package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record CommentResponse (
    Long commentId,
    Long userId,
    Long postId,
    String content
){
}
