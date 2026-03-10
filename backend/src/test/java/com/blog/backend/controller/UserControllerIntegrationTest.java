package com.blog.backend.controller;

import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.UserSignupRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("성공: 회원가입 시 유저 정보와 프로필 이미지가 정상 처리된다")
    void signup_IntegrationSuccess() throws Exception {
        // given
        UserSignupRequest signupRequest = new UserSignupRequest("newUser", "pass1234", "new@test.com", "Hello");
        String json = objectMapper.writeValueAsString(signupRequest);

        MockMultipartFile userPart = new MockMultipartFile("userJoinRequest", "",
                MediaType.APPLICATION_JSON_VALUE, json.getBytes(StandardCharsets.UTF_8));

        MockMultipartFile imagePart = new MockMultipartFile("profileImage", "test.jpg",
                MediaType.IMAGE_JPEG_VALUE, "test-image-content".getBytes());

        // when & then
        mockMvc.perform(multipart("/api/users/signup")
                        .file(userPart)
                        .file(imagePart))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newUser"));
    }

    @Test
    @DisplayName("성공: 프로필 기본 정보 조회 API 호출")
    void getProfileBasic_Success() throws Exception {
        // given
        User savedUser = userRepository.save(User.builder().username("profileUser").email("p@test.com").password("1234").build());

        // when & then
        mockMvc.perform(get("/api/users/profile/basic/{userId}", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("profileUser"));
    }
}