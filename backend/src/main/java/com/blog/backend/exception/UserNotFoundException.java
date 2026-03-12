package com.blog.backend.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String type, String value) {
        super("해당 유저를 찾을 수 없습니다. (" + type + ": " + value + ")");
    }
}
