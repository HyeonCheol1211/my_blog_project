package com.blog.backend.controller;

import com.blog.backend.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeController {
    private final LikeService likeService;

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deleteLike(
            @PathVariable Long postId,
            Authentication authentication
    ){
        Long userId = Long.parseLong(authentication.getName());
        likeService.deleteLike(postId, userId);
        return ResponseEntity.noContent().build();
    }

}
