package com.blog.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class GetPostsResponse {
    private Long postId;
    private Long userId;
    private Long categoryId;
    private String title;
    private String content;
    private boolean publicStatus;
}
