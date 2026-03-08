package com.blog.backend.controller;

import com.blog.backend.dto.AddFollowRequest;
import com.blog.backend.dto.FollowResponse;
import com.blog.backend.dto.FollowerResponse;
import com.blog.backend.dto.FollowingResponse;
import com.blog.backend.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
@Transactional
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

    @GetMapping("/{username}/followerList")
    public ResponseEntity<List<FollowerResponse>> getFollowerList(
            @PathVariable String username){
        List<FollowerResponse> FollowerList = followService.getFollowerList(username);
        return ResponseEntity.ok(FollowerList);
    }

    @GetMapping("/{username}/followingList")
    public ResponseEntity<List<FollowingResponse>> getFollowingList(
            @PathVariable String username){
        List<FollowingResponse> FollowerList = followService.getFollowingList(username);
        return ResponseEntity.ok(FollowerList);
    }
}
