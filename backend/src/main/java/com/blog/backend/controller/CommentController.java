package com.blog.backend.controller;

import com.blog.backend.dto.AddCommentRequest;
import com.blog.backend.dto.CommentDetailResponse;
import com.blog.backend.dto.CommentResponse;
import com.blog.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseEntity<CommentResponse> addComment(
            @RequestBody AddCommentRequest addCommentRequest,
            @PathVariable Long postId, Authentication authentication){
        String username = null;
        if(authentication != null){
            username = authentication.getName();
        }
        CommentResponse commentResponse = commentService.addComment(addCommentRequest, postId, username);
        return ResponseEntity.ok(commentResponse);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody AddCommentRequest addCommentRequest,
            Authentication authentication){
        String username = null;
        if(authentication != null){
            username = authentication.getName();
        }
        CommentResponse commentResponse = commentService.updateComment(commentId, addCommentRequest, username);
        return ResponseEntity.ok(commentResponse);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentResponse> deleteComment(
            @PathVariable Long commentId,
            Authentication authentication
    ){
        String username = authentication.getName();
        CommentResponse commentResponse = commentService.deleteComment(commentId, username);
        return ResponseEntity.ok(commentResponse);
    }

    @GetMapping
    public ResponseEntity<List<CommentDetailResponse>> getComments(Authentication authentication){
        String username = authentication.getName();
        List<CommentDetailResponse> commentsDetailResponse = commentService.getComments(username);
        return ResponseEntity.ok(commentsDetailResponse);
    }
}
