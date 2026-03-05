package com.blog.backend.controller;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.Like;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CategoryRepository;
import com.blog.backend.domain.repository.LikeRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.exception.UserNotFoundException;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LikeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    private Post savedPost;
    @BeforeEach
    void setUp(){
        User user1 = User.builder()
                .username("testUser")
                .password("1234")
                .email("test@test.com")
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .username("testUser2")
                .password("12342")
                .email("test2@test.com")
                .build();
        userRepository.save(user2);

        Category category1 = Category.builder()
                .name("테스트 카테고리1")
                .user(user1)
                .build();
        categoryRepository.save(category1);

        Post post1 = Post.builder()
                .title("첫번째 게시글")
                .user(user1)
                .content("카테고리1에 들어가는 첫번째 게시글")
                .category(category1)
                .publicStatus(true)
                .build();
        this.savedPost = post1;
        postRepository.save(post1);
        category1.increaseCount();

        Like like = Like.builder()
                .user(user2)
                .post(post1)
                .build();
        likeRepository.save(like);
    }



    @Test
    @DisplayName("로그인 O, 좋아요 추가")
    @WithMockUser(username = "testUser2")
    void addLike() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/likes/" + savedPost.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(pretty(result));
    }

    @Test
    @DisplayName("로그인 O, 본인 좋아요 삭제")
    @WithMockUser(username = "testUser2")
    void deleteLike_login_self() throws Exception{
        MvcResult result = mockMvc.perform(delete("/api/likes/" + savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        entityManager.flush();
        entityManager.clear();

        String username = "testUser2";
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException(username));
        Optional<Like> deletedLike = likeRepository.findByUserAndPost(user, savedPost);
        assertThat(deletedLike).isEmpty();

        System.out.println(pretty(result));
    }

    @Test
    @DisplayName("로그인 O, 본인 좋아요 삭제")
    @WithMockUser(username = "testUser")
    void deleteLike_login_notSelf() throws Exception{
        MvcResult result = mockMvc.perform(delete("/api/likes/" + savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String username = "testUser";
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException(username));
        Optional<Like> deletedLike = likeRepository.findByUserAndPost(user, savedPost);
        assertThat(deletedLike).isEmpty();
        System.out.println(pretty(result));
    }



    private String pretty(MvcResult result) throws Exception {
        String rawJsonResponse = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

        Object jsonObject = objectMapper.readValue(rawJsonResponse, Object.class);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
    }
}