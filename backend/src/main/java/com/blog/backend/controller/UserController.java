package com.blog.backend.controller;

import com.blog.backend.dto.*;
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

    @GetMapping("/profile/{targetUsername}")
    public ResponseEntity<ProfileResponse> getProfile(
            @PathVariable String targetUsername,
            Authentication authentication){
        String username = null;
        if(authentication != null) {
            username = authentication.getName();
        }
        ProfileResponse profileResponse = userService.getProfile(targetUsername, username);
        return ResponseEntity.ok(profileResponse);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestPart(value="userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @RequestPart(value="profileImage", required = false) MultipartFile multipartFile,
            Authentication authentication){
        String username = authentication.getName();
        UserResponse userResponse = userService.updateProfile(userUpdateRequest, multipartFile, username);
        return ResponseEntity.ok(userResponse);
    }
}
