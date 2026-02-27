package com.blog.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UpdatePostRequest {
    private String categoryName;
    private String title;
    private String content;
    private boolean publicStatus;
}
