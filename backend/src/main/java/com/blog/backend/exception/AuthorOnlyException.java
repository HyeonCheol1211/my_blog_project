package com.blog.backend.exception;

public class AuthorOnlyException extends RuntimeException{
    public AuthorOnlyException(Long userId){
        super("작성자만 접근할 수 있습니다. (작성자 ID : " + userId + ")");
    }
}
