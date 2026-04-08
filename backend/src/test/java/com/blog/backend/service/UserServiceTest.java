package com.blog.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CategoryRepository;
import com.blog.backend.domain.repository.FollowRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.CategoryResponse;
import com.blog.backend.dto.FollowerResponse;
import com.blog.backend.dto.FollowingResponse;
import com.blog.backend.dto.PostResponse;
import com.blog.backend.dto.ProfileBasicResponse;
import com.blog.backend.dto.ProfileExtraResponse;
import com.blog.backend.dto.UserUpdateRequest;
import com.blog.backend.exception.DuplicateEmailException;
import com.blog.backend.exception.UserNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserDetailsService userDetailsService;
    @Mock private UserRepository userRepository;
    @Mock private FollowRepository followRepository;
    @Mock private PostRepository postRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

    @BeforeEach
    void setUp() throws Exception {
        Field fileDir = UserService.class.getDeclaredField("fileDir");
        fileDir.setAccessible(true);
        fileDir.set(userService, "/tmp/");
    }

    @Test
    void getProfileBasicShouldIncludeFollowCounts() {
        User user = user(1L, "author");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(followRepository.countByFollower(user)).thenReturn(7L);
        when(followRepository.countByFollowing(user)).thenReturn(5L);

        ProfileBasicResponse response = userService.getProfileBasic(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.followingCount()).isEqualTo(7L);
        assertThat(response.followerCount()).isEqualTo(5L);
    }

    @Test
    void getProfileExtraShouldMarkFollowingState() {
        when(followRepository.existsByFollower_IdAndFollowing_Id(2L, 1L)).thenReturn(true);
        when(postRepository.countByUser_Id(1L)).thenReturn(10L);
        when(postRepository.countByUser_IdAndPublicStatus(1L, true)).thenReturn(6L);

        ProfileExtraResponse response = userService.getProfileExtra(1L, 2L);

        assertThat(response.isFollowing()).isTrue();
        assertThat(response.postAllCount()).isEqualTo(10L);
        assertThat(response.postPublicCount()).isEqualTo(6L);
    }

    @Test
    void updateProfileShouldRejectDuplicateEmail() {
        User user = user(1L, "author");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("dup@test.com")).thenReturn(true);

        assertThatThrownBy(
                        () ->
                                userService.updateProfile(
                                        UserUpdateRequest.builder()
                                                .email("dup@test.com")
                                                .bio("bio")
                                                .build(),
                                        null,
                                        1L))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void updateProfileShouldAllowSameEmail() {
        User user = user(1L, "author");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.updateProfile(
                UserUpdateRequest.builder().email("author@test.com").bio("updated bio").build(),
                null,
                1L);

        assertThat(user.getEmail()).isEqualTo("author@test.com");
        assertThat(user.getBio()).isEqualTo("updated bio");
        verify(userRepository, never()).existsByEmail("author@test.com");
    }

    @Test
    void updateProfileShouldKeepExistingEmailWhenEmailIsNull() {
        User user = user(1L, "author");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.updateProfile(UserUpdateRequest.builder().bio("updated bio").build(), null, 1L);

        assertThat(user.getEmail()).isEqualTo("author@test.com");
        assertThat(user.getBio()).isEqualTo("updated bio");
    }

    @Test
    void getUserPostsShouldReturnAllPostsForOwner() {
        List<PostResponse> expected =
                List.of(
                        PostResponse.builder()
                                .id(1L)
                                .title("title-1")
                                .content("content-1")
                                .authorId(1L)
                                .author("author")
                                .publicStatus(true)
                                .likeCount(3L)
                                .profileImageUrl("/images/profiles/basic_profile_image.png")
                                .build(),
                        PostResponse.builder()
                                .id(2L)
                                .title("title-2")
                                .content("content-2")
                                .authorId(1L)
                                .author("author")
                                .publicStatus(false)
                                .likeCount(0L)
                                .profileImageUrl("/images/profiles/basic_profile_image.png")
                                .build());
        when(postRepository.findUserPosts(1L, 1L)).thenReturn(expected);

        List<PostResponse> responses = userService.getUserPosts(1L, 1L);

        assertThat(responses).hasSize(2);
        assertThat(responses).isEqualTo(expected);
    }

    @Test
    void getCategoryListShouldHidePrivateOnlyCategoryFromVisitor() {
        List<CategoryResponse> expected =
                List.of(
                        CategoryResponse.builder()
                                .id(1L)
                                .categoryName("public")
                                .postsCount(2L)
                                .build());
        when(categoryRepository.findCategoryResponses(1L, 99L)).thenReturn(expected);

        List<CategoryResponse> responses = userService.getCategoryList(1L, 99L);

        assertThat(responses).hasSize(1);
        assertThat(responses).isEqualTo(expected);
    }

    @Test
    void getFollowersShouldReturnProjectionResponses() {
        List<FollowerResponse> expected =
                List.of(
                        FollowerResponse.builder()
                                .followerId(2L)
                                .username("reader")
                                .profileImageUrl("/images/profiles/basic_profile_image.png")
                                .build());
        when(followRepository.findFollowerResponsesByFollowingId(1L)).thenReturn(expected);

        List<FollowerResponse> responses = userService.getFollowers(1L);

        assertThat(responses).isEqualTo(expected);
    }

    @Test
    void getFollowingsShouldReturnProjectionResponses() {
        List<FollowingResponse> expected =
                List.of(
                        FollowingResponse.builder()
                                .followingId(3L)
                                .username("writer")
                                .profileImageUrl("/images/profiles/basic_profile_image.png")
                                .build());
        when(followRepository.findFollowingResponsesByFollowerId(1L)).thenReturn(expected);

        List<FollowingResponse> responses = userService.getFollowings(1L);

        assertThat(responses).isEqualTo(expected);
    }

    @Test
    void getProfileBasicShouldThrowWhenUserMissing() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getProfileBasic(100L))
                .isInstanceOf(UserNotFoundException.class);
    }

    private User user(Long id, String username) {
        return User.builder()
                .id(id)
                .username(username)
                .email(username + "@test.com")
                .password("pw")
                .bio("bio")
                .profileImageUrl("/images/profiles/basic_profile_image.png")
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Category category(Long id, String name, User user, Long count) {
        return Category.builder().id(id).name(name).user(user).count(count).build();
    }
}
