package com.blog.backend.controller;

import com.blog.backend.domain.Post;
import com.blog.backend.dto.AddPostRequest;
import com.blog.backend.dto.GetPostsResponse;
import com.blog.backend.dto.UpdatePostRequest;
import com.blog.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    @GetMapping("/my-list")
    public ResponseEntity<List<GetPostsResponse>> getMyPosts(Authentication authentication){
        String username = authentication.getName();
        List<Post> posts = postService.getMyPosts(username);
        List<GetPostsResponse> getPostResponse = posts.stream()
                .map(p-> GetPostsResponse.builder()
                .postId(p.getId())
                        .userId(p.getUser().getId())
                        .categoryId(p.getCategory().getId())
                        .title(p.getTitle())
                        .content(p.getContent())
                        .publicStatus(p.isPublicStatus())
                        .build()
        ).toList();
        return ResponseEntity.ok(getPostResponse);
    }


    @PostMapping("/add")
    public ResponseEntity<String> addPost(
            @RequestBody AddPostRequest addPostRequest
            , Authentication authentication){
        String username = authentication.getName();
        postService.addPost(username, addPostRequest);
        return ResponseEntity.ok("글이 추가되었습니다.");
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long postId,
            Authentication authentication){
        String username = authentication.getName();
        postService.deletePost(username, postId);
        return ResponseEntity.ok("삭제가 완료되었습니다.");
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<String> updatePost(
            @RequestBody UpdatePostRequest updatePostRequest,
            @PathVariable Long postId,
            Authentication authentication){
        String username = authentication.getName();
        postService.updatePost(username, updatePostRequest, postId);
        return ResponseEntity.ok("수정이 완료되었습니다.");
    }
}
