package com.blog.backend.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.support.MySqlContainerTestSupport;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest extends MySqlContainerTestSupport {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldCheckUsernameAvailability() throws Exception {
        mockMvc.perform(get("/api/auth/check-username/{username}", "new-user"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectDuplicateEmailCheck() throws Exception {
        saveUser("author", "author@test.com", "{noop}pw");

        mockMvc.perform(get("/api/auth/check-email/{email}", "author@test.com"))
                .andExpect(status().isConflict())
                .andExpect(
                        jsonPath("$.message").value(org.hamcrest.Matchers.containsString("이미 존재")));
    }

    @Test
    void shouldSignupWithoutProfileImage() throws Exception {
        MockMultipartFile request =
                new MockMultipartFile(
                        "userSignupRequest",
                        "",
                        APPLICATION_JSON_VALUE,
                        """
                        {"username":"signup-user","password":"pw","email":"signup@test.com","bio":"hello"}
                        """
                                .getBytes());

        mockMvc.perform(multipart("/api/auth/signup").file(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("signup-user"))
                .andExpect(jsonPath("$.email").value("signup@test.com"));
    }

    @Test
    void shouldLoginAndReturnToken() throws Exception {
        saveUser(
                "login-user",
                "login@test.com",
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                        .encode("pw"));

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(APPLICATION_JSON)
                                .content("{\"username\":\"login-user\",\"password\":\"pw\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.userId").isNumber());
    }

    private static final String APPLICATION_JSON_VALUE = MediaType.APPLICATION_JSON_VALUE;

    private void saveUser(String username, String email, String password) {
        userRepository.saveAndFlush(
                User.builder()
                        .username(username)
                        .email(email)
                        .password(password)
                        .profileImageUrl("/images/profiles/basic_profile_image.png")
                        .build());
    }
}
