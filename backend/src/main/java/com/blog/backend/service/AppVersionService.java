package com.blog.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.blog.backend.dto.AppVersionResponse;

@Service
public class AppVersionService {

    @Value("${app.meta.name:my-blog-backend}")
    private String appName;

    @Value("${app.meta.version:unknown}")
    private String version;

    @Value("${app.meta.commit-sha:unknown}")
    private String commitSha;

    @Value("${app.meta.built-at:unknown}")
    private String builtAt;

    @Value("${app.meta.deployed-at:unknown}")
    private String deployedAt;

    public AppVersionResponse getVersion() {
        return AppVersionResponse.builder()
                .appName(appName)
                .version(version)
                .commitSha(commitSha)
                .builtAt(builtAt)
                .deployedAt(deployedAt)
                .build();
    }
}
