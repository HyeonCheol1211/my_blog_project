package com.blog.backend.controller;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.Comment;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CategoryRepository;
import com.blog.backend.domain.repository.CommentRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.AddPostRequest;
import com.blog.backend.dto.UpdatePostRequest;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EntityManager entityManager;

    private Post savedPost;
    private Post privatedPost;

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
        this.savedPost = post1;
        postRepository.save(post1);
        category1.increaseCount();

        Comment comment1 = Comment.builder()
                .post(post1)
                .content("첫번째 게시글의 첫번째 댓글")
                .user(user2)
                .build();

        commentRepository.save(comment1);

        Post post2 = Post.builder()
                .user(user1)
                .category(category1)
                .publicStatus(true)
                .content("카테고리1에 들어가는 두번째 게시글")
                .title("두번째 게시글")
                .build();
        postRepository.save(post2);
        category1.increaseCount();



        Post post3 = Post.builder()
                .user(user2)
                .category(category3)
                .publicStatus(false)
                .content("카테고리3에 들어가는 비밀글")
                .title("첫번째 비밀글")
                .build();
        postRepository.save(post3);
        privatedPost = post3;
        category3.increaseCount();

    }

    @Test
    @DisplayName("로그인 O, 내 게시글 목록 O")
    @WithMockUser(username = "testUser")
    void getMyPosts_login_notNull() throws Exception{
        MvcResult result = mockMvc.perform(get("/api/posts/my-list")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("첫번째 게시글"))
                .andExpect(jsonPath("$[0].content").value("카테고리1에 들어가는 첫번째 게시글"))
                .andExpect(jsonPath("$[0].author").value("testUser"))
                .andExpect(jsonPath("$[0].categoryName").value("테스트 카테고리1"))
                .andExpect(jsonPath("$[0].publicStatus").value(true))
                .andExpect(jsonPath("$[1].title").value("두번째 게시글"))
                .andExpect(jsonPath("$[1].content").value("카테고리1에 들어가는 두번째 게시글"))
                .andExpect(jsonPath("$[1].author").value("testUser"))
                .andExpect(jsonPath("$[1].categoryName").value("테스트 카테고리1"))
                .andExpect(jsonPath("$[1].publicStatus").value(true))
                .andReturn();

        System.out.println(pretty(result));
    }

    @Test
    @DisplayName("로그인 O, 내 게시글 목록 X")
    @WithMockUser(username = "testUser3")
    void getMyPosts_login_null() throws Exception{
        MvcResult result = mockMvc.perform(get("/api/posts/my-list")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
                .andReturn();

        System.out.println(pretty(result));
    }

    @Test
    @DisplayName("로그인 X, 내 게시글 목록")
    void getMyPosts_notLogin() throws Exception{
        MvcResult result = mockMvc.perform(get("/api/posts/my-list")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        System.out.println(pretty(result));
    }

    @Test
    @DisplayName("로그인 O, 게시글 작성")
    @WithMockUser(username = "testUser")
    void addPost_login() throws Exception {
        AddPostRequest addPostRequest = AddPostRequest.builder()
                .title("새로운 테스트 글")
                .content("이것은 통합 테스트로 작성된 글입니다.")
                .categoryName("개발")
                .publicStatus(true)
                .build();

        String jsonRequest = objectMapper.writeValueAsString(addPostRequest);

        MvcResult result = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("새로운 테스트 글"))
                .andExpect(jsonPath("$.content").value("이것은 통합 테스트로 작성된 글입니다."))
                .andExpect(jsonPath("$.author").value("testUser"))
                .andReturn();

        System.out.println(pretty(result));
    }

    @Test
    @DisplayName("로그인 X, 게시글 작성")
    void addPost_notLogin() throws Exception {
        AddPostRequest addPostRequest = AddPostRequest.builder()
                .title("새로운 테스트 글")
                .content("이것은 통합 테스트로 작성된 글입니다.")
                .categoryName("개발")
                .publicStatus(true)
                .build();

        String jsonRequest = objectMapper.writeValueAsString(addPostRequest);
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("로그인 O, 자기 게시글 삭제")
    @WithMockUser("testUser")
    void deletePost_login_self() throws Exception{
        MvcResult result = mockMvc.perform(delete("/api/posts/" + savedPost.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공적으로 삭제되었습니다."))
                .andExpect(jsonPath("$.id").value(savedPost.getId()))
                .andReturn();

        entityManager.flush();
        entityManager.clear();

        Optional<Post> deletedPost = postRepository.findById(savedPost.getId());
        assertThat(deletedPost).isEmpty();

        System.out.println(pretty(result));
    }

    @Test
    @DisplayName("로그인 X, 다른 게시글 삭제")
    void deletePost_notLogin() throws Exception{
        mockMvc.perform(delete("/api/posts/" + savedPost.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("로그인 O, 다른 게시글 삭제")
    @WithMockUser("testUser2")
    void deletePost_login_notSelf() throws Exception{
        mockMvc.perform(delete("/api/posts/" + savedPost.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("로그인 O, 없는 게시글 삭제")
    @WithMockUser("testUser2")
    void deletePost_login_notId() throws Exception{
        mockMvc.perform(delete("/api/posts/" + 100000)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("로그인 X, 업데이트")
    void updatePost_notLogin() throws Exception{
        UpdatePostRequest updatePostRequest = UpdatePostRequest.builder()
                .categoryName("테스트 카테고리1")
                .content("카테고리1에 들어가는 첫번째 게시글의 첫 수정")
                .publicStatus(true)
                .title("첫번째 게시글의 수정본")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(updatePostRequest);

        mockMvc.perform(put("/api/posts/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("로그인 O, 자기 글 업데이트")
    @WithMockUser(username = "testUser")
    void updatePost_login_self() throws Exception{
        UpdatePostRequest updatePostRequest = UpdatePostRequest.builder()
                .categoryName("테스트 카테고리1234")
                .content("카테고리1에 들어가는 첫번째 게시글의 첫 수정")
                .publicStatus(true)
                .title("첫번째 게시글의 수정본")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(updatePostRequest);

        MvcResult result = mockMvc.perform(put("/api/posts/" + savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedPost.getId()))
                .andExpect(jsonPath("$.title").value("첫번째 게시글의 수정본"))
                .andExpect(jsonPath("$.content").value("카테고리1에 들어가는 첫번째 게시글의 첫 수정"))
                .andExpect(jsonPath("$.author").value("testUser"))
                .andExpect(jsonPath("$.categoryName").value("테스트 카테고리1234"))
                .andExpect(jsonPath("$.publicStatus").value(true))
                .andReturn();
        System.out.println(pretty(result));
    }

    @Test
    @DisplayName("로그인 O, 다른 사람 글 업데이트")
    @WithMockUser(username = "testUser2")
    void updatePost_login_notSelf() throws Exception{
        UpdatePostRequest updatePostRequest = UpdatePostRequest.builder()
                .categoryName("테스트 카테고리1234")
                .content("카테고리1에 들어가는 첫번째 게시글의 첫 수정")
                .publicStatus(true)
                .title("첫번째 게시글의 수정본")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(updatePostRequest);

        MvcResult result = mockMvc.perform(put("/api/posts/" + savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("작성자만 접근할 수 있습니다. (작성자 ID : " + savedPost.getUser().getId() + ")"))
                .andReturn();
        System.out.println(pretty(result));
    }


    @Test
    @DisplayName("로그인 O, 다른 사람 글 업데이트")
    @WithMockUser(username = "testUser2")
    void updatePost_login_notNum() throws Exception{
        UpdatePostRequest updatePostRequest = UpdatePostRequest.builder()
                .categoryName("테스트 카테고리1234")
                .content("카테고리1에 들어가는 첫번째 게시글의 첫 수정")
                .publicStatus(true)
                .title("첫번째 게시글의 수정본")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(updatePostRequest);

        MvcResult result = mockMvc.perform(put("/api/posts/" + 100000)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("해당 게시글을 찾을 수 없습니다. (게시글 ID: 100000)"))
                .andReturn();
        System.out.println(pretty(result));
    }

    @Test
    @DisplayName("로그인 X, 공개글 조회")
    void getPost_notLogin_public() throws Exception{
        MvcResult result = mockMvc.perform(get("/api/posts/" + savedPost.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("첫번째 게시글"))
                .andExpect(jsonPath("$.content").value("카테고리1에 들어가는 첫번째 게시글"))
                .andExpect(jsonPath("$.categoryName").value("테스트 카테고리1"))
                .andReturn();

        System.out.println(pretty(result));
    }

    @Test
    @DisplayName("로그인 X, 비밀글 조회")
    void getPost_notLogin_private() throws Exception{
        MvcResult result = mockMvc.perform(get("/api/posts/" + privatedPost.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("작성자만 접근할 수 있습니다. (작성자 ID : " + privatedPost.getUser().getId() + ")"))
                .andReturn();

        System.out.println(pretty(result));
    }

    @Test
    @DisplayName("로그인 O, 다른 사람 비밀글 조회")
    @WithMockUser(username = "testUser")
    void getPost_notLogin_notSelf_private() throws Exception{
        MvcResult result = mockMvc.perform(get("/api/posts/" + privatedPost.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("작성자만 접근할 수 있습니다. (작성자 ID : " + privatedPost.getUser().getId() + ")"))
                .andReturn();

        System.out.println(pretty(result));
    }

    @Test
    @DisplayName("로그인 O, 본인 비밀글 조회")
    @WithMockUser(username = "testUser2")
    void getPost_notLogin_self_private() throws Exception{
        MvcResult result = mockMvc.perform(get("/api/posts/" + privatedPost.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("첫번째 비밀글"))
                .andExpect(jsonPath("$.content").value("카테고리3에 들어가는 비밀글"))
                .andExpect(jsonPath("$.categoryName").value("테스트 카테고리3"))
                .andExpect(jsonPath("$.publicStatus").value(false))
                .andReturn();

        System.out.println(pretty(result));
    }

    @Test
    @DisplayName("로그인 X, 전체 글 조회")
    void getPosts() throws Exception{
        MvcResult result = mockMvc.perform(get("/api/posts/list")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andReturn();

        System.out.println(pretty(result));
    }

    private String pretty(MvcResult result) throws Exception {
        String rawJsonResponse = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

        Object jsonObject = objectMapper.readValue(rawJsonResponse, Object.class);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
    }
}