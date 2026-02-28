package com.blog.backend.controller;

import com.blog.backend.dto.AddCommentRequest;
import com.blog.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/add/{postId}")
    public ResponseEntity<String> addComment(
            @RequestBody AddCommentRequest addCommentRequest,
            @PathVariable Long postId, Authentication authentication){
        String username = authentication.getName();
        commentService.addComment(addCommentRequest, postId, username);
        return ResponseEntity.ok("댓글이 추가되었습니다.");
    }
}
