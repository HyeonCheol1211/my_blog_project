package com.blog.backend.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String username){
        super("해당 유저를 찾을 수 없습니다. (유저 이름: " + username + ")" );
    }
}
