package com.blog.backend.exception;

public class DuplicateUsernameException extends RuntimeException {
    public DuplicateUsernameException(String username) {
        super("해당 유저이름을 가진 사용자가 이미 존재합니다. (유저이름 : " + username + ")");
    }
}
