package com.blog.backend.controller;

import com.blog.backend.dto.LikeResponse;
import com.blog.backend.dto.LikeUserResponse;
import com.blog.backend.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeController {
    private final LikeService likeService;


    @PostMapping("/{postId}")
    public ResponseEntity<LikeResponse> addLike(
            @PathVariable Long postId,
            Authentication authentication
    ){
        String username = authentication.getName();
        LikeResponse likeResponse = likeService.addLike(postId, username);
        return ResponseEntity.ok(likeResponse);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<LikeResponse> deleteLike(
            @PathVariable Long postId,
            Authentication authentication
    ){
        String username = authentication.getName();
        LikeResponse likeResponse = likeService.deleteLike(postId, username);
        return ResponseEntity.ok(likeResponse);
    }

    @GetMapping("/{postId}/list")
    public ResponseEntity<List<LikeUserResponse>> getLikeUserList(
            @PathVariable Long postId,
            Authentication authentication){
        String username = authentication.getName();
        List<LikeUserResponse> likeUserResponses = likeService.getLikeUserList(postId, username);
        return ResponseEntity.ok(likeUserResponses);
    }

}
