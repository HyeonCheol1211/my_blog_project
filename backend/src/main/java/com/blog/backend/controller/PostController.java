package com.blog.backend.controller;

import com.blog.backend.dto.*;
import com.blog.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/my-list")
    public ResponseEntity<List<PostResponse>> getMyPosts(Authentication authentication){
        String username = null;
        if(authentication != null) {
            username = authentication.getName();
        }
        List<PostResponse> getPostResponse = postService.getMyPosts(username);
        return ResponseEntity.ok(getPostResponse);
    }


    @PostMapping("")
    public ResponseEntity<PostResponse> addPost(
            @RequestBody AddPostRequest addPostRequest
            , Authentication authentication){
        String username = authentication.getName();
        PostResponse postResponse = postService.addPost(username, addPostRequest);
        return ResponseEntity.ok(postResponse);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<DeletePostResponse> deletePost(
            @PathVariable Long postId,
            Authentication authentication){
        String username = null;
        if(authentication != null) {
            username = authentication.getName();
        }
        DeletePostResponse deletePostResponse= postService.deletePost(username, postId);
        return ResponseEntity.ok(deletePostResponse);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @RequestBody UpdatePostRequest updatePostRequest,
            @PathVariable Long postId,
            Authentication authentication){
        String username = authentication.getName();
        PostResponse postResponse = postService.updatePost(username, updatePostRequest, postId);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPost(@PathVariable Long postId, Authentication authentication){
       String username = null;
       if(authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")){
           username = authentication.getName();
       }
       PostDetailResponse postDetailResponse = postService.getPost(postId, username);
       return ResponseEntity.ok(postDetailResponse);
    }

    @GetMapping("/list")
    public ResponseEntity<List<PostResponse>> getPosts(){
        List<PostResponse> postsResponse = postService.getPosts();
        return ResponseEntity.ok(postsResponse);
    }
}
