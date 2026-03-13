package com.blog.backend.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.blog.backend.dto.*;
import com.blog.backend.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> addPost(
            @RequestBody AddPostRequest addPostRequest, @AuthenticationPrincipal Long userId) {

        PostResponse postResponse = postService.addPost(addPostRequest, userId);
        return ResponseEntity.ok(postResponse);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId, @AuthenticationPrincipal Long userId) {

        postService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody UpdatePostRequest updatePostRequest,
            @AuthenticationPrincipal Long userId) {

        PostResponse postResponse = postService.updatePost(postId, updatePostRequest, userId);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPost(
            @PathVariable Long postId, @AuthenticationPrincipal Long userId) {
        PostDetailResponse postDetailResponse = postService.getPost(postId, userId);
        return ResponseEntity.ok(postDetailResponse);
    }

    @GetMapping("/list")
    public ResponseEntity<List<PostResponse>> getPosts(
            @RequestParam(required = false, defaultValue = "latest") String sort,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDateTime startDate,
            @PageableDefault(size = 10) Pageable pageable) {
        List<PostResponse> postsResponse = postService.getPosts(sort, startDate, pageable);
        return ResponseEntity.ok(postsResponse);
    }

    @GetMapping("/{postId}/likes")
    public ResponseEntity<List<LikeUserResponse>> getLikeUserList(
            @PathVariable Long postId, @AuthenticationPrincipal Long userId) {

        List<LikeUserResponse> likeUserResponses = postService.getLikeUserList(postId, userId);
        return ResponseEntity.ok(likeUserResponses);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getPostComments(@PathVariable Long postId) {
        List<CommentResponse> commentResponses = postService.getPostComments(postId);
        return ResponseEntity.ok(commentResponses);
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long postId,
            @RequestBody AddCommentRequest addCommentRequest,
            @AuthenticationPrincipal Long userId) {

        CommentResponse commentResponse = postService.addComment(postId, addCommentRequest, userId);
        return ResponseEntity.ok(commentResponse);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<LikeResponse> addLike(
            @PathVariable Long postId, @AuthenticationPrincipal Long userId) {

        LikeResponse likeResponse = postService.addLike(postId, userId);
        return ResponseEntity.ok(likeResponse);
    }
}
