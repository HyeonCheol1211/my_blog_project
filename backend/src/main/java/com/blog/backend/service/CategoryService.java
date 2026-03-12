package com.blog.backend.service;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.repository.CategoryRepository;
import com.blog.backend.domain.repository.LikeRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.dto.CategoryResponse;
import com.blog.backend.dto.PostResponse;
import com.blog.backend.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;

    public List<CategoryResponse> getCategoryList(Long userId) {

        return categoryRepository.findAllByUser_Id(userId).stream()
                .map(
                        category ->
                                CategoryResponse.builder()
                                        .id(category.getId())
                                        .categoryName(category.getName())
                                        .build())
                .toList();
    }

    public List<PostResponse> getCategoryPosts(Long categoryId, Long userId) {
        Category category =
                categoryRepository
                        .findById(categoryId)
                        .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        Long authorId = category.getUserId();
        List<Post> posts = List.of();
        if (authorId.equals(userId)) {
            posts = postRepository.findAllByCategory_Id(categoryId);
        }

        if (!authorId.equals(userId)) {
            posts = postRepository.findAllByCategory_IdAndPublicStatus(categoryId, true);
        }

        return posts.stream()
                .map(
                        p ->
                                PostResponse.builder()
                                        .id(p.getId())
                                        .title(p.getTitle())
                                        .content(p.getContent())
                                        .authorId(p.getUserId())
                                        .author(p.getUsername())
                                        .publicStatus(p.isPublicStatus())
                                        .createdAt(p.getCreatedAt())
                                        .likeCount(likeRepository.countByPost(p))
                                        .profileImageUrl(p.getProfileImage())
                                        .build())
                .toList();
    }
}
