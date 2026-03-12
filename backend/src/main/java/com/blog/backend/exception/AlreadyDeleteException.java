package com.blog.backend.exception;

public class AlreadyDeleteException extends RuntimeException {
    public AlreadyDeleteException() {
        super("이미 처리된 요청입니다.");
    }
}
