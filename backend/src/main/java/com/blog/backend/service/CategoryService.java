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

    public List<CategoryResponse> getCategoryList(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException(username));

        return categoryRepository.findAllByUser(user)
                .stream()
                .map(category -> CategoryResponse.builder()
                        .categoryName(category.getName())
                        .build()
                )
                .toList();
    }
}
