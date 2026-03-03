package com.blog.backend.exception;

public class PostNotFoundException extends RuntimeException{
    public PostNotFoundException(Long postId){
        super("해당 게시글을 찾을 수 없습니다. (게시글 ID: " + postId + ")" );
    }
}
