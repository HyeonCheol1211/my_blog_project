package com.blog.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.blog.backend.dto.*;
import com.blog.backend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile/basic/{userId}")
    public ResponseEntity<ProfileBasicResponse> getProfileBasic(@PathVariable Long userId) {
        ProfileBasicResponse profileBasicResponse = userService.getProfileBasic(userId);
        return ResponseEntity.ok(profileBasicResponse);
    }

    @GetMapping("/profile/extra/{userId}")
    public ResponseEntity<ProfileExtraResponse> getProfileExtra(
            @PathVariable Long userId, @AuthenticationPrincipal Long loginUserId) {
        ProfileExtraResponse profileExtraResponse =
                userService.getProfileExtra(userId, loginUserId);
        return ResponseEntity.ok(profileExtraResponse);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestPart(value = "userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profileImage", required = false) MultipartFile multipartFile,
            @AuthenticationPrincipal Long userId) {

        UserResponse userResponse =
                userService.updateProfile(userUpdateRequest, multipartFile, userId);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<FollowerResponse>> getFollowers(@PathVariable Long userId) {
        List<FollowerResponse> FollowerList = userService.getFollowers(userId);
        return ResponseEntity.ok(FollowerList);
    }

    @GetMapping("/{userId}/followings")
    public ResponseEntity<List<FollowingResponse>> getFollowings(@PathVariable Long userId) {
        List<FollowingResponse> FollowerList = userService.getFollowings(userId);
        return ResponseEntity.ok(FollowerList);
    }

    @GetMapping("/{userId}/posts")
    public ResponseEntity<List<PostResponse>> getUserPosts(
            @PathVariable Long userId, @AuthenticationPrincipal Long loginUserId) {
        List<PostResponse> getPostResponse = userService.getUserPosts(userId, loginUserId);
        return ResponseEntity.ok(getPostResponse);
    }

    @GetMapping("{userId}/categories")
    public ResponseEntity<List<CategoryResponse>> getCategoryList(
            @PathVariable Long userId, @AuthenticationPrincipal Long loginUserId) {

        List<CategoryResponse> categoryResponses = userService.getCategoryList(userId, loginUserId);
        return ResponseEntity.ok(categoryResponses);
    }
}
