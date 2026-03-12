package com.blog.backend.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Long categoryId) {
        super("해당 카테고리를 찾을 수 없습니다. (Category ID" + categoryId + ")");
    }
}
