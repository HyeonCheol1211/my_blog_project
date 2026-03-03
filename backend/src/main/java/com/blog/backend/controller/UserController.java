package com.blog.backend.controller;

import com.blog.backend.dto.UserJoinRequest;
import com.blog.backend.dto.UserLoginRequest;
import com.blog.backend.dto.UserResponse;
import com.blog.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<UserResponse> join(@RequestBody UserJoinRequest userJoinRequest){
        UserResponse userResponse = userService.join(userJoinRequest);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequest userLoginRequest){
        String token = userService.login(userLoginRequest.username(), userLoginRequest.password());
        return ResponseEntity.ok().body(token);
    }
}
