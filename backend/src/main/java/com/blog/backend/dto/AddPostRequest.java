package com.blog.backend.dto;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import lombok.Builder;

@Builder
public record AddPostRequest(
    String categoryName,
    String title,
    String content,
    boolean publicStatus
){

    public AddPostRequest(String categoryName) {
        this(categoryName, null, null, false);
    }

    public Post toEntity(User user, Category category) {
        return Post.builder()
                .user(user)
                .category(category)
                .title(title)
                .content(content)
                .publicStatus(publicStatus)
                .build();
    }
}
