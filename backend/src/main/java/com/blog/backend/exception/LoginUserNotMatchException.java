package com.blog.backend.exception;

public class LoginUserNotMatchException extends RuntimeException{
    public LoginUserNotMatchException(Long requestId, Long loginId){
        super("요청 ID : " + requestId + ", 로그인 ID : " + loginId);
    }
}
