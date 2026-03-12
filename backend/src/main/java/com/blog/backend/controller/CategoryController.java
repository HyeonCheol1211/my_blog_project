package com.blog.backend.controller;

import com.blog.backend.dto.CategoryResponse;
import com.blog.backend.dto.PostResponse;
import com.blog.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
            @AuthenticationPrincipal Long userId) {

        List<CategoryResponse> categoryResponses = categoryService.getCategoryList(userId);
        return ResponseEntity.ok(categoryResponses);
    }

    @GetMapping("/{categoryId}/posts")
    public ResponseEntity<List<PostResponse>> getCategoryPosts(
            @PathVariable Long categoryId, @AuthenticationPrincipal Long userId) {
        List<PostResponse> postResponses = categoryService.getCategoryPosts(categoryId, userId);
        return ResponseEntity.ok(postResponses);
    }
}
