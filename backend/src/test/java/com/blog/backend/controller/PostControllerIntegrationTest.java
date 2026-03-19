package com.blog.backend.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.blog.backend.domain.Like;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CategoryRepository;
import com.blog.backend.domain.repository.CommentRepository;
import com.blog.backend.domain.repository.LikeRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.utils.JwtUtil;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PostControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private LikeRepository likeRepository;
    @Autowired private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        likeRepository.deleteAll();
        commentRepository.deleteAll();
        postRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldReturnPublicPostWithoutAuthentication() throws Exception {
        User author = saveUser("author");
        Category category = saveCategory(author, "daily", 1L);
        Post post = savePost(author, category, true);

        mockMvc.perform(get("/api/posts/{postId}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.authorId").value(author.getId()))
                .andExpect(jsonPath("$.liked").value(false))
                .andExpect(jsonPath("$.categoryName").value("daily"));
    }

    @Test
    void shouldRejectPrivatePostWithoutAuthentication() throws Exception {
        User author = saveUser("secret-author");
        Category category = saveCategory(author, "secret", 1L);
        Post post = savePost(author, category, false);

        mockMvc.perform(get("/api/posts/{postId}", post.getId()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("작성자만 접근")));
    }

    @Test
    void shouldReturnConflictWhenLikeAlreadyExists() throws Exception {
        User author = saveUser("author");
        User reader = saveUser("reader");
        Category category = saveCategory(author, "likes", 1L);
        Post post = savePost(author, category, true);
        likeRepository.saveAndFlush(Like.builder().post(post).user(reader).build());

        mockMvc.perform(
                        post("/api/posts/{postId}/like", post.getId())
                                .header(AUTHORIZATION, bearer(reader.getId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 처리된 요청입니다."));
    }

    @Test
    void shouldReturnUnauthorizedForEmptyBearerToken() throws Exception {
        User author = saveUser("author");
        Category category = saveCategory(author, "auth", 1L);
        Post post = savePost(author, category, true);

        mockMvc.perform(post("/api/posts/{postId}/like", post.getId()).header(AUTHORIZATION, "Bearer "))
                .andExpect(status().isUnauthorized());
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
                        .profileImage("/images/profiles/basic_profile_image.png")
                        .build());
    }

    private Category saveCategory(User user, String name, Long count) {
        return categoryRepository.saveAndFlush(
                Category.builder().user(user).name(name).count(count).build());
    }

    private Post savePost(User user, Category category, boolean publicStatus) {
        return postRepository.saveAndFlush(
                Post.builder()
                        .user(user)
                        .category(category)
                        .title("title")
                        .content("content")
                        .publicStatus(publicStatus)
                        .build());
    }
}
