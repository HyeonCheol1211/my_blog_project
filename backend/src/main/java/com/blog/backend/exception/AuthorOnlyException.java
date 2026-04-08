package com.blog.backend.exception;

public class AuthorOnlyException extends RuntimeException {
    public AuthorOnlyException() {
        super("작성자만 접근할 수 있습니다.");
    }
}
