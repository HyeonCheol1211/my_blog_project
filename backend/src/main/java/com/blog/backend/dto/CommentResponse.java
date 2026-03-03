package com.blog.backend.dto;

import lombok.Builder;

@Builder
public record CommentResponse (
    Long commentId,
    String author,
    Long postId,
    String content
){
}
