package com.blog.backend.exception;

public class AlreadyAddException extends RuntimeException {
    public AlreadyAddException() {
        super("이미 처리된 요청입니다.");
    }
}
