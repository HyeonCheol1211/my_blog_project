package com.blog.backend.controller;

import com.blog.backend.dto.*;
import com.blog.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("")
    public ResponseEntity<PostResponse> addPost(
            @RequestBody AddPostRequest addPostRequest, Authentication authentication) {
        String username = authentication.getName();
        PostResponse postResponse = postService.addPost(addPostRequest, username);
        return ResponseEntity.ok(postResponse);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<DeletePostResponse> deletePost(
            @PathVariable Long postId,
            Authentication authentication) {
        String username = authentication.getName();
        DeletePostResponse deletePostResponse = postService.deletePost(postId, username);
        return ResponseEntity.ok(deletePostResponse);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody UpdatePostRequest updatePostRequest,
            Authentication authentication) {
        String username = authentication.getName();
        PostResponse postResponse = postService.updatePost(postId, updatePostRequest, username);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPost(
            @PathVariable Long postId,
            Authentication authentication) {
        String username = null;
        if (authentication != null) {
            username = authentication.getName();
        }
        PostDetailResponse postDetailResponse = postService.getPost(postId, username);
        return ResponseEntity.ok(postDetailResponse);
    }

    @GetMapping("/list")
    public ResponseEntity<List<PostResponse>> getPosts() {
        List<PostResponse> postsResponse = postService.getPosts();
        return ResponseEntity.ok(postsResponse);
    }

    @GetMapping("/{postId}/likes")
    public ResponseEntity<List<LikeUserResponse>> getLikeUserList(
            @PathVariable Long postId,
            Authentication authentication) {
        String username = authentication.getName();
        List<LikeUserResponse> likeUserResponses = postService.getLikeUserList(postId, username);
        return ResponseEntity.ok(likeUserResponses);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getPostComments(@PathVariable Long postId){
        List<CommentResponse> commentResponses = postService.getPostComments(postId);
        return ResponseEntity.ok(commentResponses);
    }
}
