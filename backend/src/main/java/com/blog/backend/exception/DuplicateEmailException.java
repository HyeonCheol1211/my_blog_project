package com.blog.backend.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("해당 이메일을 가진 사용자가 이미 존재합니다. (이메일 : " + email + ")");
    }
}
