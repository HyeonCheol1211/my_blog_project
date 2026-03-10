package com.blog.backend.controller;

import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.FollowRepository;
import com.blog.backend.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class FollowControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private FollowRepository followRepository;

    private User follower;
    private User following;

    @BeforeEach
    void setUp() {
        follower = userRepository.save(User.builder().username("follower").email("f@test.com").password("1234").build());
        following = userRepository.save(User.builder().username("following").email("ing@test.com").password("1234").build());
    }

    @Test
    @WithMockUser(username = "follower")
    @DisplayName("성공: 팔로우 API 호출 시 팔로우 관계가 생성된다")
    void addFollow_IntegrationSuccess() throws Exception {
        mockMvc.perform(post("/api/follows/{userId}", following.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followerUsername").value("follower"))
                .andExpect(jsonPath("$.followingUsername").value("following"));

        assertThat(followRepository.findAll()).hasSize(1);
    }

    @Test
    @WithMockUser(username = "follower")
    @DisplayName("성공: 언팔로우 API 호출 시 팔로우 관계가 삭제된다")
    void deleteFollow_IntegrationSuccess() throws Exception {
        // 먼저 팔로우 처리
        mockMvc.perform(post("/api/follows/{userId}", following.getId()));

        // 언팔로우 요청
        mockMvc.perform(delete("/api/follows/{userId}", following.getId()))
                .andExpect(status().isOk());

        assertThat(followRepository.findAll()).isEmpty();
    }
}