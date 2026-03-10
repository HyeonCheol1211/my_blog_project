package com.blog.backend.controller;

import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.LikeRepository;
import com.blog.backend.domain.repository.PostRepository;
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
class LikeControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private LikeRepository likeRepository;

    private User savedUser;
    private Post savedPost;

    @BeforeEach
    void setUp() {
        savedUser = userRepository.save(User.builder().username("testUser").email("test@test.com").password("1234").build());
        savedPost = postRepository.save(Post.builder().title("Title").content("Content").user(savedUser).build());
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("성공: 좋아요 API 호출 시 Like 데이터가 생성된다")
    void addLike_IntegrationSuccess() throws Exception {
        mockMvc.perform(post("/api/likes/{postId}", savedPost.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(savedPost.getId()))
                .andExpect(jsonPath("$.userId").value(savedUser.getId()));

        assertThat(likeRepository.findAll()).hasSize(1);
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("성공: 좋아요 취소 API 호출 시 Like 데이터가 삭제된다")
    void deleteLike_IntegrationSuccess() throws Exception {
        // 먼저 좋아요 생성
        mockMvc.perform(post("/api/likes/{postId}", savedPost.getId()));

        // 취소(삭제) 요청
        mockMvc.perform(delete("/api/likes/{postId}", savedPost.getId()))
                .andExpect(status().isOk());

        assertThat(likeRepository.findAll()).isEmpty();
    }
}