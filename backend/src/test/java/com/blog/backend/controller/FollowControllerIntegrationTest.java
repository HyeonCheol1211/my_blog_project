package com.blog.backend.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.blog.backend.domain.Follow;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.FollowRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.utils.JwtUtil;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FollowControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private FollowRepository followRepository;

    @BeforeEach
    void setUp() {
        followRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldAddFollow() throws Exception {
        User target = saveUser("target");
        User login = saveUser("login");

        mockMvc.perform(
                        post("/api/follows/{userId}", target.getId())
                                .header(AUTHORIZATION, bearer(login.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followingUsername").value("target"));
    }

    @Test
    void shouldDeleteFollow() throws Exception {
        User target = saveUser("target");
        User login = saveUser("login");
        followRepository.saveAndFlush(Follow.builder().follower(login).following(target).build());

        mockMvc.perform(
                        delete("/api/follows/{userId}", target.getId())
                                .header(AUTHORIZATION, bearer(login.getId())))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldRejectDuplicateFollow() throws Exception {
        User target = saveUser("target");
        User login = saveUser("login");
        followRepository.saveAndFlush(Follow.builder().follower(login).following(target).build());

        mockMvc.perform(
                        post("/api/follows/{userId}", target.getId())
                                .header(AUTHORIZATION, bearer(login.getId())))
                .andExpect(status().isConflict());
    }

    private String bearer(Long userId) {
        return "Bearer " + jwtUtil.createToken(userId);
    }

    private User saveUser(String username) {
        return userRepository.saveAndFlush(
                User.builder()
                        .username(username)
                        .email(username + "@test.com")
                        .password("pw")
                        .profileImageUrl("/images/profiles/basic_profile_image.png")
                        .build());
    }
}
