package com.blog.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.blog.backend.support.MySqlContainerTestSupport;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AppVersionControllerIntegrationTest extends MySqlContainerTestSupport {

    @Autowired private MockMvc mockMvc;

    @Test
    void shouldExposeApplicationVersionMetadataWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/version"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appName").value("my-blog-backend-test"))
                .andExpect(jsonPath("$.version").value("test-version"))
                .andExpect(jsonPath("$.commitSha").value("test-commit"))
                .andExpect(jsonPath("$.builtAt").value("2026-04-21T00:00:00Z"))
                .andExpect(jsonPath("$.deployedAt").value("2026-04-21T00:01:00Z"));
    }
}
