package com.blog.backend.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CategoryRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.utils.JwtUtil;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoryControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldReturnOnlyPublicPostsForGuest() throws Exception {
        User author = saveUser("author");
        Category category = saveCategory(author, "daily");
        savePost(author, category, "public-title", true);
        savePost(author, category, "private-title", false);

        mockMvc.perform(get("/api/categories/{categoryId}/posts", category.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("public-title"));
    }

    @Test
    void shouldReturnAllPostsForOwner() throws Exception {
        User author = saveUser("author");
        Category category = saveCategory(author, "daily");
        savePost(author, category, "public-title", true);
        savePost(author, category, "private-title", false);

        mockMvc.perform(
                        get("/api/categories/{categoryId}/posts", category.getId())
                                .header(AUTHORIZATION, bearer(author.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
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

    private Category saveCategory(User user, String name) {
        return categoryRepository.saveAndFlush(
                Category.builder().user(user).name(name).count(0L).build());
    }

    private void savePost(User user, Category category, String title, boolean publicStatus) {
        category.increaseCount();
        postRepository.saveAndFlush(
                Post.builder()
                        .user(user)
                        .category(category)
                        .title(title)
                        .content("content")
                        .publicStatus(publicStatus)
                        .build());
    }
}
