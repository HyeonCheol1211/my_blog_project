package com.blog.backend.controller;

import com.blog.backend.domain.Follow;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.FollowRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.AddFollowRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class FollowControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager EntityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    private User savedUser;
    @BeforeEach
    void setup(){
        User user1 = User.builder()
                .username("testUser")
                .password("1234")
                .email("test@test.com")
                .build();
        savedUser = user1;
        userRepository.save(user1);

        User user2 = User.builder()
                .username("testUser2")
                .password("12342")
                .email("test2@test.com")
                .build();
        userRepository.save(user2);

        User user3 = User.builder()
                .username("testUser3")
                .password("12343")
                .email("test3@test.com")
                .build();
        userRepository.save(user3);

        Follow follow3_1 = Follow.builder()
                .user1(user3)
                .user2(user1)
                .build();

        followRepository.save(follow3_1);
    }


    @Test
    @DisplayName("로그인 O, 팔로우 추가")
    @WithMockUser(username = "testUser2")
    void addFollow_login() throws Exception{
        AddFollowRequest addFollowRequest = AddFollowRequest.builder()
                .username(savedUser.getUsername())
                .build();

        String jsonRequest = objectMapper.writeValueAsString(addFollowRequest);

        MvcResult result = mockMvc.perform(post("/api/follows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(pretty(result));
    }

    @Test
    @DisplayName("로그인 X, 팔로우 추가")
    void addFollow_notLogin() throws Exception{
        AddFollowRequest addFollowRequest = AddFollowRequest.builder()
                .username(savedUser.getUsername())
                .build();

        String jsonRequest = objectMapper.writeValueAsString(addFollowRequest);

        mockMvc.perform(post("/api/follows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("로그인 O, 팔로우 삭제")
    @WithMockUser(username = "testUser3")
    void deleteFollow_login() throws Exception{
        AddFollowRequest addFollowRequest = AddFollowRequest.builder()
                .username(savedUser.getUsername())
                .build();

        String jsonRequest = objectMapper.writeValueAsString(addFollowRequest);

        MvcResult result = mockMvc.perform(delete("/api/follows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(pretty(result));
    }

    @Test
    @DisplayName("로그인 X, 팔로우 삭제")
    void deleteFollow_notLogin() throws Exception{
        AddFollowRequest addFollowRequest = AddFollowRequest.builder()
                .username(savedUser.getUsername())
                .build();

        String jsonRequest = objectMapper.writeValueAsString(addFollowRequest);

        mockMvc.perform(delete("/api/follows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    private String pretty(MvcResult result) throws Exception {
        String rawJsonResponse = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

        Object jsonObject = objectMapper.readValue(rawJsonResponse, Object.class);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
    }

}