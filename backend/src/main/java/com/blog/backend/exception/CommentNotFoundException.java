package com.blog.backend.exception;

public class CommentNotFoundException extends RuntimeException{
    public CommentNotFoundException(Long commentId){
        super("해당 아이디의 댓글이 존재하지 않습니다. (댓글 ID : " + commentId + ")");
    }
}
