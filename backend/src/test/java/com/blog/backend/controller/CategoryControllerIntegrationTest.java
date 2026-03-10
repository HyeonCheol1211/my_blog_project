package com.blog.backend.controller;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CategoryRepository;
import com.blog.backend.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // 테스트 후 DB 롤백
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = userRepository.save(User.builder()
                .username("testUser")
                .email("test@test.com")
                .password("password")
                .build());

        categoryRepository.save(Category.builder().name("Tech").user(savedUser).build());
        categoryRepository.save(Category.builder().name("Life").user(savedUser).build());
    }

    @Test
    @WithMockUser(username = "testUser") // 가짜 인증 유저 설정
    @DisplayName("성공: API 호출 시 카테고리 목록 JSON을 반환한다")
    void getCategoryList_IntegrationSuccess() throws Exception {
        mockMvc.perform(get("/api/categories/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].categoryName").value("Tech"))
                .andExpect(jsonPath("$[1].categoryName").value("Life"));
    }

    @Test
    @DisplayName("실패: 인증되지 않은 사용자가 호출 시 403(혹은 401) 에러 발생")
    void getCategoryList_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/categories/list"))
                .andExpect(status().isForbidden()); // Security 설정에 따라 401/403 결정됨
    }
}