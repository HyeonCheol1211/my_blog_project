package com.blog.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.blog.backend.dto.FollowResponse;
import com.blog.backend.service.FollowService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
@Transactional
public class FollowController {
    private final FollowService followService;

    @PostMapping("/{userId}")
    public ResponseEntity<FollowResponse> addFollow(
            @PathVariable Long userId, @AuthenticationPrincipal Long loginUserId) {
        FollowResponse followResponse = followService.addFollow(userId, loginUserId);
        return ResponseEntity.ok(followResponse);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteFollow(
            @PathVariable Long userId, @AuthenticationPrincipal Long loginUserId) {
        followService.deleteFollow(userId, loginUserId);
        return ResponseEntity.noContent().build();
    }
}
