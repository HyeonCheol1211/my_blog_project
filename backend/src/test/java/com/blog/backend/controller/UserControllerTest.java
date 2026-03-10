package com.blog.backend.controller;

import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.UserSignupRequest;
import com.blog.backend.dto.UserLoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp(){
        User user = User.builder()
                .username("testUser")
                .email("test@test.com")
                .password("123456")
                .build();
        userRepository.save(user);
    }

    @Test
    @DisplayName("아이디 중복 X, 회원가입")
    void join_unique() throws Exception {
        UserSignupRequest userSignupRequest = UserSignupRequest.builder()
                .username("테스트유저1")
                .email("testUser1@naver.com")
                .password("1234")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(userSignupRequest);

        MockMultipartFile jsonPart = new MockMultipartFile(
                "userJoinRequest",
                "",
                "application/json",
                jsonRequest.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "profileImage",
                "my_face.png",
                "image/png",
                "fake-image-byte-data".getBytes()
        );


        MvcResult result = mockMvc.perform(multipart("/api/users/join")
                        .file(jsonPart)
                        .file(imagePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("테스트유저1"))
                .andExpect(jsonPath("$.email").value("testUser1@naver.com"))
                .andReturn();

        System.out.println(pretty(result));
    }
    @Test
    @DisplayName("아이디 중복 O, 회원가입")
    void join_notUnique() throws Exception {
        UserSignupRequest userSignupRequest = UserSignupRequest.builder()
                .username("testUser")
                .email("test123@test.com")
                .password("123456")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(userSignupRequest);

        MockMultipartFile jsonPart = new MockMultipartFile(
                "userJoinRequest",
                "",
                "application/json",
                jsonRequest.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "profileImage",
                "my_face.png",
                "image/png",
                "fake-image-byte-data".getBytes()
        );


        MvcResult result = mockMvc.perform(multipart("/api/users/join")
                        .file(jsonPart)
                        .file(imagePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        System.out.println(pretty(result));
    }

    @Test
    @DisplayName("로그인 시도(올바른)")
    void login_success() throws Exception{
        UserLoginRequest userLoginRequest = UserLoginRequest.builder()
                .username("testUser")
                .password("123456")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(userLoginRequest);

        String token = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn().toString();

        System.out.println(token);
    }

    @Test
    @DisplayName("로그인 비밀번호 틀림")
    void login_fail_password() throws Exception{
        UserLoginRequest userLoginRequest = UserLoginRequest.builder()
                .username("testUser")
                .password("12345")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(userLoginRequest);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("로그인 유저이름 틀림")
    void login_fail_username() throws Exception{
        UserLoginRequest userLoginRequest = UserLoginRequest.builder()
                .username("testUser123")
                .password("123456")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(userLoginRequest);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }



    private String pretty(MvcResult result) throws Exception {
        String rawJsonResponse = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

        Object jsonObject = objectMapper.readValue(rawJsonResponse, Object.class);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
    }
}