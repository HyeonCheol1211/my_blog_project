package com.blog.backend.controller;

import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CategoryRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.AddPostRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        userRepository.save(User.builder()
                .username("testUser")
                .email("test@test.com")
                .password("password")
                .build());
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("성공: 게시글 작성 시 카테고리가 자동 생성되고 글이 저장된다")
    void addPost_IntegrationSuccess() throws Exception {
        // given
        AddPostRequest request = new AddPostRequest("Java", "제목", "내용", true);

        // when
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.categoryName").value("Java"));

        // then
        assertThat(postRepository.findAll()).hasSize(1);
        assertThat(categoryRepository.findByNameAndUser("Java",
                userRepository.findByUsername("testUser").get())).isPresent();
    }
}