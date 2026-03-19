package com.blog.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.backend.service.LikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeController {
    private final LikeService likeService;

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deleteLike(
            @PathVariable Long postId, @AuthenticationPrincipal Long userId) {

        likeService.deleteLike(postId, userId);
        return ResponseEntity.noContent().build();
    }
}
