package com.blog.backend.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.Like;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CategoryRepository;
import com.blog.backend.domain.repository.LikeRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.utils.JwtUtil;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LikeControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private LikeRepository likeRepository;

    @BeforeEach
    void setUp() {
        likeRepository.deleteAll();
        postRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldDeleteLikeAndReturnNoContent() throws Exception {
        User author = saveUser("author");
        User liker = saveUser("liker");
        Post post = savePost(author);
        likeRepository.saveAndFlush(Like.builder().user(liker).post(post).build());

        mockMvc.perform(
                        delete("/api/likes/{postId}", post.getId())
                                .header(AUTHORIZATION, bearer(liker.getId())))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenLikeAlreadyDeleted() throws Exception {
        User liker = saveUser("liker");

        mockMvc.perform(
                        delete("/api/likes/{postId}", 999L)
                                .header(AUTHORIZATION, bearer(liker.getId())))
                .andExpect(status().isNotFound());
    }

    private String bearer(Long userId) {
        return "Bearer " + jwtUtil.createToken(userId);
    }

    private User saveUser(String username) {
        return userRepository.saveAndFlush(
                User.builder()
                        .username(username)
                        .email(username + "@test.com")
                        .password("pw")
                        .profileImageUrl("/images/profiles/basic_profile_image.png")
                        .build());
    }

    private Post savePost(User author) {
        Category category =
                categoryRepository.saveAndFlush(
                        Category.builder().name("daily").user(author).count(1L).build());
        return postRepository.saveAndFlush(
                Post.builder()
                        .user(author)
                        .category(category)
                        .title("title")
                        .content("content")
                        .publicStatus(true)
                        .build());
    }
}
