package com.blog.backend.exception;

public class PasswordNotCorrectException extends RuntimeException {
    public PasswordNotCorrectException(String password){
        super("비밀번호가 일치하지 않습니다. (입력값 : " + password + ")");
    }
}
