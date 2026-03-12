package com.blog.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blog.backend.domain.repository.CategoryRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.CategoryResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public List<CategoryResponse> getCategoryList(Long userId) {

        return categoryRepository.findAllByUser_Id(userId).stream()
                .map(
                        category ->
                                CategoryResponse.builder().categoryName(category.getName()).build())
                .toList();
    }
}
