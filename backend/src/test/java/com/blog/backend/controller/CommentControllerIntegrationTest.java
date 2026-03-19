package com.blog.backend.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.Comment;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CategoryRepository;
import com.blog.backend.domain.repository.CommentRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.utils.JwtUtil;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CommentControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldUpdateCommentForAuthor() throws Exception {
        User author = saveUser("author");
        Comment comment = saveComment(author, "old");

        mockMvc.perform(
                        put("/api/comments/{commentId}", comment.getId())
                                .header(AUTHORIZATION, bearer(author.getId()))
                                .contentType(APPLICATION_JSON)
                                .content("{\"content\":\"new\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("new"));
    }

    @Test
    void shouldRejectCommentUpdateByDifferentUser() throws Exception {
        User author = saveUser("author");
        User other = saveUser("other");
        Comment comment = saveComment(author, "old");

        mockMvc.perform(
                        put("/api/comments/{commentId}", comment.getId())
                                .header(AUTHORIZATION, bearer(other.getId()))
                                .contentType(APPLICATION_JSON)
                                .content("{\"content\":\"new\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnMyComments() throws Exception {
        User author = saveUser("author");
        saveComment(author, "hello");

        mockMvc.perform(get("/api/comments").header(AUTHORIZATION, bearer(author.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("hello"));
    }

    @Test
    void shouldDeleteOwnComment() throws Exception {
        User author = saveUser("author");
        Comment comment = saveComment(author, "hello");

        mockMvc.perform(
                        delete("/api/comments/{commentId}", comment.getId())
                                .header(AUTHORIZATION, bearer(author.getId())))
                .andExpect(status().isNoContent());
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

    private Comment saveComment(User user, String content) throws Exception {
        Category category =
                categoryRepository.saveAndFlush(
                        Category.builder().name("daily").user(user).count(1L).build());
        Post post =
                postRepository.saveAndFlush(
                        Post.builder()
                                .user(user)
                                .category(category)
                                .title("title")
                                .content("content")
                                .publicStatus(true)
                                .build());
        Comment comment = Comment.builder().user(user).post(post).content(content).build();
        commentRepository.saveAndFlush(comment);
        if (comment.getId() == null) {
            Field id = Comment.class.getDeclaredField("id");
            id.setAccessible(true);
            id.set(comment, 1L);
        }
        return comment;
    }
}
