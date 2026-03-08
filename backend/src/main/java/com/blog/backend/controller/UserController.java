package com.blog.backend.controller;

import com.blog.backend.dto.ProfileResponse;
import com.blog.backend.dto.UserJoinRequest;
import com.blog.backend.dto.UserLoginRequest;
import com.blog.backend.dto.UserResponse;
import com.blog.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<UserResponse> join(
            @RequestPart(value = "userJoinRequest") UserJoinRequest userJoinRequest,
            @RequestPart(value = "profileImage", required = false) MultipartFile multipartFile){
        UserResponse userResponse = userService.join(userJoinRequest, multipartFile);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequest userLoginRequest){
        String token = userService.login(userLoginRequest.username(), userLoginRequest.password());
        return ResponseEntity.ok().body(token);
    }

    @GetMapping("/profile/{username2}")
    public ResponseEntity<ProfileResponse> getProfile(
            @PathVariable String username2,
            Authentication authentication){
        String username1 = null;
        if(authentication != null) {
            username1 = authentication.getName();
        }
        ProfileResponse profileResponse = userService.getProfile(username1, username2);
        return ResponseEntity.ok(profileResponse);
    }
}
