package com.blog.backend.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import com.blog.backend.domain.Comment;
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
    void shouldReturnLatestPostListWithProjectionFields() throws Exception {
        User olderAuthor = saveUser("older-author");
        User latestAuthor = saveUser("latest-author");
        Category olderCategory = saveCategory(olderAuthor, "older", 1L);
        Category latestCategory = saveCategory(latestAuthor, "latest", 1L);
        Post olderPost = savePost(olderAuthor, olderCategory, true);
        Post latestPost = savePost(latestAuthor, latestCategory, true);

        likeRepository.saveAndFlush(Like.builder().post(latestPost).user(olderAuthor).build());

        mockMvc.perform(get("/api/posts/list").param("postSortType", "LATEST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(latestPost.getId()))
                .andExpect(jsonPath("$[0].authorId").value(latestAuthor.getId()))
                .andExpect(jsonPath("$[0].author").value("latest-author"))
                .andExpect(
                        jsonPath("$[0].profileImageUrl")
                                .value("/images/profiles/basic_profile_image.png"))
                .andExpect(jsonPath("$[0].likeCount").value(1))
                .andExpect(jsonPath("$[1].id").value(olderPost.getId()))
                .andExpect(jsonPath("$[1].likeCount").value(0));
    }

    @Test
    void shouldReturnPopularPostListOrderedByLikeCount() throws Exception {
        User author = saveUser("author");
        User likerOne = saveUser("liker-one");
        User likerTwo = saveUser("liker-two");
        Category category = saveCategory(author, "popular", 2L);
        Post lessLikedPost = savePost(author, category, true);
        Post moreLikedPost = savePost(author, category, true);

        likeRepository.saveAndFlush(Like.builder().post(lessLikedPost).user(likerOne).build());
        likeRepository.saveAndFlush(Like.builder().post(moreLikedPost).user(likerOne).build());
        likeRepository.saveAndFlush(Like.builder().post(moreLikedPost).user(likerTwo).build());

        mockMvc.perform(get("/api/posts/list").param("postSortType", "WEEKLY_LIKE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(moreLikedPost.getId()))
                .andExpect(jsonPath("$[0].likeCount").value(2))
                .andExpect(jsonPath("$[1].id").value(lessLikedPost.getId()))
                .andExpect(jsonPath("$[1].likeCount").value(1));
    }

    @Test
    void shouldExcludePrivatePostsFromList() throws Exception {
        User author = saveUser("author");
        Category category = saveCategory(author, "visibility", 2L);
        savePost(author, category, true);
        savePost(author, category, false);

        mockMvc.perform(get("/api/posts/list").param("postSortType", "LATEST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].publicStatus").value(true));
    }

    @Test
    void shouldApplyPaginationToPostList() throws Exception {
        User author = saveUser("author");
        Category category = saveCategory(author, "page", 3L);
        Post firstPost = savePost(author, category, true);
        Post secondPost = savePost(author, category, true);
        Post thirdPost = savePost(author, category, true);

        mockMvc.perform(
                        get("/api/posts/list")
                                .param("postSortType", "LATEST")
                                .param("page", "0")
                                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(thirdPost.getId()))
                .andExpect(jsonPath("$[1].id").value(secondPost.getId()));

        mockMvc.perform(
                        get("/api/posts/list")
                                .param("postSortType", "LATEST")
                                .param("page", "1")
                                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(firstPost.getId()));
    }

    @Test
    void shouldReturnBadRequestForInvalidPostSortType() throws Exception {
        mockMvc.perform(get("/api/posts/list").param("postSortType", "LIKES"))
                .andExpect(status().isBadRequest())
                .andExpect(
                        content()
                                .string(
                                        org.hamcrest.Matchers.not(
                                                org.hamcrest.Matchers.blankString())));
    }

    @Test
    void shouldRejectPrivatePostWithoutAuthentication() throws Exception {
        User author = saveUser("secret-author");
        Category category = saveCategory(author, "secret", 1L);
        Post post = savePost(author, category, false);

        mockMvc.perform(get("/api/posts/{postId}", post.getId()))
                .andExpect(status().isForbidden())
                .andExpect(
                        jsonPath("$.message")
                                .value(org.hamcrest.Matchers.containsString("작성자만 접근")));
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

        mockMvc.perform(
                        post("/api/posts/{postId}/like", post.getId())
                                .header(AUTHORIZATION, "Bearer "))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectPrivateCommentsWithoutAuthentication() throws Exception {
        User author = saveUser("secret-author");
        Category category = saveCategory(author, "secret", 1L);
        Post post = savePost(author, category, false);
        saveComment(author, post, "hidden");

        mockMvc.perform(get("/api/posts/{postId}/comments", post.getId()))
                .andExpect(status().isForbidden())
                .andExpect(
                        jsonPath("$.message")
                                .value(org.hamcrest.Matchers.containsString("작성자만 접근")));
    }

    @Test
    void shouldRejectPrivateCommentsForDifferentUser() throws Exception {
        User author = saveUser("secret-author");
        User reader = saveUser("reader");
        Category category = saveCategory(author, "secret", 1L);
        Post post = savePost(author, category, false);
        saveComment(author, post, "hidden");

        mockMvc.perform(
                        get("/api/posts/{postId}/comments", post.getId())
                                .header(AUTHORIZATION, bearer(reader.getId())))
                .andExpect(status().isForbidden())
                .andExpect(
                        jsonPath("$.message")
                                .value(org.hamcrest.Matchers.containsString("작성자만 접근")));
    }

    @Test
    void shouldReturnPrivateCommentsForAuthor() throws Exception {
        User author = saveUser("secret-author");
        Category category = saveCategory(author, "secret", 1L);
        Post post = savePost(author, category, false);
        saveComment(author, post, "hidden");

        mockMvc.perform(
                        get("/api/posts/{postId}/comments", post.getId())
                                .header(AUTHORIZATION, bearer(author.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("hidden"));
    }

    @Test
    void shouldRejectAddingCommentToPrivatePostForDifferentUser() throws Exception {
        User author = saveUser("secret-author");
        User reader = saveUser("reader");
        Category category = saveCategory(author, "secret", 1L);
        Post post = savePost(author, category, false);

        mockMvc.perform(
                        post("/api/posts/{postId}/comment", post.getId())
                                .header(AUTHORIZATION, bearer(reader.getId()))
                                .contentType(APPLICATION_JSON)
                                .content("{\"content\":\"blocked\"}"))
                .andExpect(status().isForbidden())
                .andExpect(
                        jsonPath("$.message")
                                .value(org.hamcrest.Matchers.containsString("작성자만 접근")));
    }

    @Test
    void shouldAllowAddingCommentToPrivatePostForAuthor() throws Exception {
        User author = saveUser("secret-author");
        Category category = saveCategory(author, "secret", 1L);
        Post post = savePost(author, category, false);

        mockMvc.perform(
                        post("/api/posts/{postId}/comment", post.getId())
                                .header(AUTHORIZATION, bearer(author.getId()))
                                .contentType(APPLICATION_JSON)
                                .content("{\"content\":\"mine\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorId").value(author.getId()))
                .andExpect(jsonPath("$.content").value("mine"));
    }

    @Test
    void shouldRejectAddingLikeToPrivatePostForDifferentUser() throws Exception {
        User author = saveUser("secret-author");
        User reader = saveUser("reader");
        Category category = saveCategory(author, "secret", 1L);
        Post post = savePost(author, category, false);

        mockMvc.perform(
                        post("/api/posts/{postId}/like", post.getId())
                                .header(AUTHORIZATION, bearer(reader.getId())))
                .andExpect(status().isForbidden())
                .andExpect(
                        jsonPath("$.message")
                                .value(org.hamcrest.Matchers.containsString("작성자만 접근")));
    }

    @Test
    void shouldAllowAddingLikeToPrivatePostForAuthor() throws Exception {
        User author = saveUser("secret-author");
        Category category = saveCategory(author, "secret", 1L);
        Post post = savePost(author, category, false);

        mockMvc.perform(
                        post("/api/posts/{postId}/like", post.getId())
                                .header(AUTHORIZATION, bearer(author.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(1));
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

    private Comment saveComment(User user, Post post, String content) {
        return commentRepository.saveAndFlush(
                Comment.builder().user(user).post(post).content(content).build());
    }
}
