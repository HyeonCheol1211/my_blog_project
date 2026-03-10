package com.blog.backend.controller;

import com.blog.backend.dto.FollowResponse;
import com.blog.backend.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
@Transactional
public class FollowController {
    private final FollowService followService;

    @PostMapping("/{userId}")
    public ResponseEntity<FollowResponse> addFollow(@PathVariable Long userId, Authentication authentication){
        String username = authentication.getName();
        FollowResponse followResponse = followService.addFollow(userId, username);
        return ResponseEntity.ok(followResponse);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<FollowResponse> deleteFollow(@PathVariable Long userId, Authentication authentication){
        String username = authentication.getName();
        FollowResponse followResponse = followService.deleteFollow(userId, username);
        return ResponseEntity.ok(followResponse);
    }
}
