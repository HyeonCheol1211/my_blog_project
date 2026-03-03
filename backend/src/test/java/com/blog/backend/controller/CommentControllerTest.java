package com.blog.backend.controller;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CategoryRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;


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

        User user3 = User.builder()
                .username("testUser3")
                .password("12343")
                .email("test3@test.com")
                .build();
        userRepository.save(user3);

        Category category1 = Category.builder()
                .name("테스트 카테고리1")
                .user(user1)
                .build();

        categoryRepository.save(category1);

        Category category2 = Category.builder()
                .name("테스트 카테고리2")
                .user(user1)
                .build();

        categoryRepository.save(category2);

        Category category3 = Category.builder()
                .name("테스트 카테고리3")
                .user(user2)
                .build();

        categoryRepository.save(category3);

        Post post1 = Post.builder()
                .title("첫번째 게시글")
                .user(user1)
                .content("카테고리1에 들어가는 첫번째 게시글")
                .category(category1)
                .publicStatus(true)
                .build();

        savedPost = post1;
        postRepository.save(post1);

        Post post2 = Post.builder()
                .user(user1)
                .category(category1)
                .publicStatus(true)
                .content("카테고리1에 들어가는 두번째 게시글")
                .title("두번째 게시글")
                .build();
        postRepository.save(post2);


        Post post3 = Post.builder()
                .user(user2)
                .category(category3)
                .publicStatus(false)
                .content("카테고리3에 들어가는 비밀글")
                .title("첫번째 비밀글")
                .build();
        postRepository.save(post3);
    }



    @Test
    @DisplayName("로그인 X, 글 O, 댓글 달기")
    void addComment_notLogin_notNull() throws Exception{
        AddCommentRequest addCommentRequest = AddCommentRequest.builder()
                .content("user1의 첫번째 게시글에 다는 첫번째 댓글입니다.")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(addCommentRequest);

        mockMvc.perform(post(("/api/comments/") + savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("로그인 O, 글 O, 댓글 달기")
    @WithMockUser(username = "testUser2")
    void addComment_login_notNull() throws Exception{
        AddCommentRequest addCommentRequest = AddCommentRequest.builder()
                .content("user1의 첫번째 게시글에 다는 첫번째 댓글입니다.")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(addCommentRequest);

        mockMvc.perform(post(("/api/comments/") + savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("testUser2"))
                .andExpect(jsonPath("$.content").value("user1의 첫번째 게시글에 다는 첫번째 댓글입니다."))
                .andExpect(jsonPath("$.postId").value(savedPost.getId()));
    }

    @Test
    @DisplayName("로그인 O, 글 X, 댓글 달기")
    @WithMockUser("testUser2")
    void addComment_login_null() throws Exception{
        AddCommentRequest addCommentRequest = AddCommentRequest.builder()
                .content("user1의 첫번째 게시글에 다는 첫번째 댓글입니다.")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(addCommentRequest);

        mockMvc.perform(post(("/api/comments/") + 100000)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

}