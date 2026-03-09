package com.blog.backend.controller;

import com.blog.backend.dto.CategoryResponse;
import com.blog.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories/")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/list")
    public ResponseEntity<List<CategoryResponse>> getCategoryList(
            Authentication authentication
    ){
        String username = authentication.getName();
        List<CategoryResponse> categoryResponses = categoryService.getCategoryList(username);
        return ResponseEntity.ok(categoryResponses);
    }
}
