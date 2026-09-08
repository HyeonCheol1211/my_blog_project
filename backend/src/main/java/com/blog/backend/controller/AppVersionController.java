package com.blog.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.backend.dto.AppVersionResponse;
import com.blog.backend.service.AppVersionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/version")
public class AppVersionController {

    private final AppVersionService appVersionService;

    @GetMapping
    public ResponseEntity<AppVersionResponse> getVersion() {
        return ResponseEntity.ok(appVersionService.getVersion());
    }
}
