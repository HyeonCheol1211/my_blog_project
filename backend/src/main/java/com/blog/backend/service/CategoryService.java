package com.blog.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.dto.PostResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final PostRepository postRepository;

    public List<PostResponse> getCategoryPosts(Long categoryId, Long userId) {
        return postRepository.findPostResponsesByCategoryIdAndUserId(categoryId, userId);
    }
}
