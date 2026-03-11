package com.blog.backend.controller;

import com.blog.backend.dto.CommentDetailResponse;
import com.blog.backend.dto.CommentResponse;
import com.blog.backend.dto.UpdateCommentRequest;
import com.blog.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest updateCommentRequest,
            @AuthenticationPrincipal Long userId){
        
        CommentResponse commentResponse = commentService.updateComment(commentId, updateCommentRequest, userId);
        return ResponseEntity.ok(commentResponse);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal Long userId
    ){
        
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CommentDetailResponse>> getMyComments(@AuthenticationPrincipal Long userId){
        
        List<CommentDetailResponse> commentsDetailResponse = commentService.getMyComments(userId);
        return ResponseEntity.ok(commentsDetailResponse);
    }


}
