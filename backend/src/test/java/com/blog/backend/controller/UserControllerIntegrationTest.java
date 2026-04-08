package com.blog.backend.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.Follow;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CategoryRepository;
import com.blog.backend.domain.repository.FollowRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.utils.JwtUtil;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private FollowRepository followRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        followRepository.deleteAll();
        postRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldReturnProfileBasic() throws Exception {
        User user = saveUser("author", "author@test.com");
        User follower = saveUser("follower", "follower@test.com");
        User following = saveUser("following", "following@test.com");
        followRepository.saveAndFlush(Follow.builder().follower(follower).following(user).build());
        followRepository.saveAndFlush(Follow.builder().follower(user).following(following).build());

        mockMvc.perform(get("/api/users/profile/basic/{userId}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("author"))
                .andExpect(jsonPath("$.followerCount").value(1))
                .andExpect(jsonPath("$.followingCount").value(1));
    }

    @Test
    void shouldReturnOnlyPublicUserPostsForGuest() throws Exception {
        User user = saveUser("author", "author@test.com");
        Category category = saveCategory(user, "daily", 2L);
        savePost(user, category, "public-title", true);
        savePost(user, category, "private-title", false);

        mockMvc.perform(get("/api/users/{userId}/posts", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("public-title"));
    }

    @Test
    void shouldReturnOwnerCategoriesIncludingPrivateOnly() throws Exception {
        User user = saveUser("author", "author@test.com");
        Category publicCategory = saveCategory(user, "public", 1L);
        Category privateCategory = saveCategory(user, "private", 1L);
        savePost(user, publicCategory, "public-title", true);
        savePost(user, privateCategory, "private-title", false);

        mockMvc.perform(
                        get("/api/users/{userId}/categories", user.getId())
                                .header(AUTHORIZATION, bearer(user.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldUpdateProfileEmail() throws Exception {
        User user = saveUser("author", "author@test.com");
        MockMultipartFile request =
                new MockMultipartFile(
                        "userUpdateRequest",
                        "",
                        APPLICATION_JSON_VALUE,
                        "{\"password\":\"newpw\",\"email\":\"new@test.com\",\"bio\":\"new bio\"}"
                                .getBytes());

        mockMvc.perform(
                        multipart(HttpMethod.PUT, "/api/users/profile")
                                .file(request)
                                .header(AUTHORIZATION, bearer(user.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@test.com"));
    }

    private static final String APPLICATION_JSON_VALUE = MediaType.APPLICATION_JSON_VALUE;

    private String bearer(Long userId) {
        return "Bearer " + jwtUtil.createToken(userId);
    }

    private User saveUser(String username, String email) {
        return userRepository.saveAndFlush(
                User.builder()
                        .username(username)
                        .email(email)
                        .password("pw")
                        .bio("bio")
                        .profileImageUrl("/images/profiles/basic_profile_image.png")
                        .build());
    }

    private Category saveCategory(User user, String name, Long count) {
        return categoryRepository.saveAndFlush(
                Category.builder().user(user).name(name).count(count).build());
    }

    private void savePost(User user, Category category, String title, boolean publicStatus) {
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
