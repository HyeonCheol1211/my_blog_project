package com.blog.backend.dto;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AddPostRequest {
    private String categoryName;
    private String title;
    private String content;
    private boolean publicStatus;

    public Post toEntity(User user, Category category){
        return Post.builder()
                .user(user)
                .category(category)
                .title(title)
                .content(content)
                .publicStatus(publicStatus)
                .build();
    }
}
