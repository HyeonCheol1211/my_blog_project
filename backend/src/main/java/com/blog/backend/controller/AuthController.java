package com.blog.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.blog.backend.dto.LoginResponse;
import com.blog.backend.dto.UserLoginRequest;
import com.blog.backend.dto.UserResponse;
import com.blog.backend.dto.UserSignupRequest;
import com.blog.backend.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @GetMapping("/check-username/{username}")
    public ResponseEntity<Void> checkUsername(@PathVariable String username) {
        authService.checkUsername(username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<Void> checkEmail(@PathVariable String email) {
        authService.checkEmail(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(
            @RequestPart(value = "userSignupRequest") UserSignupRequest userSignupRequest,
            @RequestPart(value = "profileImage", required = false) MultipartFile multipartFile) {
        UserResponse userResponse = authService.signup(userSignupRequest, multipartFile);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
        LoginResponse loginResponse = authService.login(userLoginRequest);
        return ResponseEntity.ok().body(loginResponse);
    }
}
