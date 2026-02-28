package com.blog.backend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentResponse {
    private Long commentId;
    private Long userId;
    private Long postId;
    private String content;

    @Builder
    public CommentResponse(Long commentId, Long userId, Long postId, String content){
        this.commentId = commentId;
        this.userId = userId;
        this.postId = postId;
        this.content = content;
    }

}
