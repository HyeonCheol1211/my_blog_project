package com.blog.backend.controller;

import com.blog.backend.domain.User;
import com.blog.backend.dto.UserJoinRequest;
import com.blog.backend.dto.UserLoginRequest;
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
    public ResponseEntity<Long> join(@RequestBody UserJoinRequest userJoinRequest){
        User user = userJoinRequest.toEntity();
        Long userId = userService.join(user);
        return ResponseEntity.ok(userId);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequest userLoginRequest){
        String token = userService.login(userLoginRequest.getUsername(), userLoginRequest.getPassword());
        return ResponseEntity.ok().body(token);
    }
}
