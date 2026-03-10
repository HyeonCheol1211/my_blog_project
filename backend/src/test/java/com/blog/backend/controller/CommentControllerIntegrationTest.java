package com.blog.backend.controller;

import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CommentRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.AddCommentRequest;
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

// 👇 꼭 확인해야 할 static imports
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private CommentRepository commentRepository;

    private User savedUser;
    private Post savedPost;

    @BeforeEach
    void setUp() {
        savedUser = userRepository.save(User.builder().username("testUser").password("1234").email("test@test.com").build());
        savedPost = postRepository.save(Post.builder().title("Title").content("Content").user(savedUser).build());
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("성공: 댓글 등록 API 호출 시 DB에 저장되고 정보를 반환한다")
    void addComment_IntegrationSuccess() throws Exception {
        // given
        AddCommentRequest request = new AddCommentRequest("Test Comment");

        // when & then
        mockMvc.perform(post("/api/comments/post/{postId}", savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON) // 👈 수정됨
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Test Comment"))
                .andExpect(jsonPath("$.author").value("testUser"));

        // 👈 assertThat 사용을 위해 상단 import 확인
        assertThat(commentRepository.findAll()).hasSize(1);
    }
}