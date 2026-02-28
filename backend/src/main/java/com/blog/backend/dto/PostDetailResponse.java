package com.blog.backend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Builder
@Getter
public class PostDetailResponse {
    private Long id;
    private String title;
    private String content;
    private String author;
    private String categoryName;
    private boolean publicStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long likeCount;
    private List<CommentResponse> commentsResponse;

    @Builder
    public PostDetailResponse(
            Long id, String title, String content, String author, String categoryName,
            boolean publicStatus, LocalDateTime createdAt, LocalDateTime updatedAt,
            Long likeCount, List<CommentResponse> commentsResponse){
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.categoryName = categoryName;
        this.publicStatus = publicStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likeCount = likeCount;
        this.commentsResponse = commentsResponse;
    }
}
