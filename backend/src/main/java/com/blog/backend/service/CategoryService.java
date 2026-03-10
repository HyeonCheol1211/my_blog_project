package com.blog.backend.service;


import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CategoryRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.CategoryResponse;
import com.blog.backend.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public List<CategoryResponse> getCategoryList(Long userId) {

        return categoryRepository.findAllByUser_Id(userId)
                .stream()
                .map(category -> CategoryResponse.builder()
                        .categoryName(category.getName())
                        .build()
                )
                .toList();
    }
}
