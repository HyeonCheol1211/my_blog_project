package com.blog.backend.controller;

import com.blog.backend.dto.AddFollowRequest;
import com.blog.backend.dto.FollowResponse;
import com.blog.backend.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
public class FollowController {
    private final FollowService followService;
    @PostMapping
    public ResponseEntity<FollowResponse> addFollow(@RequestBody AddFollowRequest addFollowRequest, Authentication authentication){
        String username = authentication.getName();
        FollowResponse followResponse = followService.addFollow(addFollowRequest, username);
        return ResponseEntity.ok(followResponse);
    }

    @DeleteMapping
    public ResponseEntity<FollowResponse> deleteFollow(@RequestBody AddFollowRequest addFollowRequest, Authentication authentication){
        String username = authentication.getName();
        FollowResponse followResponse = followService.deleteFollow(addFollowRequest, username);
        return ResponseEntity.ok(followResponse);
    }
}
