package com.blog.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.blog.backend.domain.*;
import com.blog.backend.domain.constant.PostSortType;
import com.blog.backend.domain.repository.*;
import com.blog.backend.dto.*;
import com.blog.backend.exception.AlreadyAddException;
import com.blog.backend.exception.AuthorOnlyException;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock private PostRepository postRepository;
    @Mock private UserRepository userRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private LikeRepository likeRepository;
    @Mock private CommentRepository commentRepository;

    @InjectMocks private PostService postService;

    @Test
    void updatePostShouldMoveCategoryAndDeleteEmptyOldCategory() {
        User user = user(1L, "writer");
        Category oldCategory = category(10L, "old", user, 1L);
        Category newCategory = category(11L, "new", user, 0L);
        Post post = post(100L, user, oldCategory, false);
        UpdatePostRequest request =
                UpdatePostRequest.builder()
                        .categoryName("new")
                        .title("updated title")
                        .content("updated content")
                        .publicStatus(true)
                        .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(100L)).thenReturn(Optional.of(post));
        when(categoryRepository.findByNameAndUser("new", user)).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(likeRepository.countByPost(post)).thenReturn(3L);

        PostResponse response = postService.updatePost(100L, request, 1L);

        assertThat(post.getCategoryName()).isEqualTo("new");
        assertThat(oldCategory.getCount()).isZero();
        assertThat(post.getCategory().getCount()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("updated title");
        verify(categoryRepository).delete(oldCategory);
    }

    @Test
    void getPostShouldRejectPrivatePostForAnotherUser() {
        User author = user(1L, "author");
        Category category = category(10L, "secret", author, 1L);
        Post privatePost = post(200L, author, category, false);

        when(postRepository.findById(200L)).thenReturn(Optional.of(privatePost));

        assertThatThrownBy(() -> postService.getPost(200L, 2L))
                .isInstanceOf(AuthorOnlyException.class)
                .hasMessageContaining("작성자만 접근");

        verify(userRepository, never()).findById(2L);
    }

    @Test
    void getPostShouldRejectPrivatePostWithoutAuthentication() {
        User author = user(1L, "author");
        Category category = category(10L, "secret", author, 1L);
        Post privatePost = post(201L, author, category, false);

        when(postRepository.findById(201L)).thenReturn(Optional.of(privatePost));

        assertThatThrownBy(() -> postService.getPost(201L, null))
                .isInstanceOf(AuthorOnlyException.class)
                .hasMessageContaining("작성자만 접근");

        verify(userRepository, never()).findById(any());
    }

    @Test
    void getPostShouldIncludeLikedStateForAuthor() {
        User author = user(1L, "author");
        Category category = category(10L, "public", author, 1L);
        Post publicPost = post(300L, author, category, true);
        PostDetailResponse detailResponse =
                PostDetailResponse.builder()
                        .id(300L)
                        .title("title")
                        .content("content")
                        .authorId(1L)
                        .author("author")
                        .categoryName("public")
                        .publicStatus(true)
                        .likeCount(5L)
                        .liked(true)
                        .profileImageUrl("/images/profiles/basic_profile_image.png")
                        .build();

        when(postRepository.findById(300L)).thenReturn(Optional.of(publicPost));
        when(postRepository.findPostDetailResponse(300L, 1L)).thenReturn(detailResponse);

        PostDetailResponse response = postService.getPost(300L, 1L);

        assertThat(response.liked()).isTrue();
        assertThat(response.likeCount()).isEqualTo(5L);
        assertThat(response.authorId()).isEqualTo(1L);
    }

    @Test
    void getPostsShouldUseLatestProjectionWhenSortTypeIsLatest() {
        Pageable pageable = PageRequest.of(0, 10);
        List<PostResponse> expected =
                List.of(
                        PostResponse.builder()
                                .id(1L)
                                .title("latest")
                                .content("content")
                                .authorId(1L)
                                .author("author")
                                .publicStatus(true)
                                .likeCount(0L)
                                .profileImageUrl("/images/profiles/basic_profile_image.png")
                                .build());

        when(postRepository.findPostResponsesByLatest(pageable)).thenReturn(expected);

        List<PostResponse> result = postService.getPosts(PostSortType.LATEST, pageable);

        assertThat(result).isEqualTo(expected);
        verify(postRepository).findPostResponsesByLatest(pageable);
        verify(postRepository, never()).findPostResponsesByDateLike(any(), any());
    }

    @Test
    void getPostsShouldUsePopularProjectionForWeeklyLikeSort() {
        Pageable pageable = PageRequest.of(0, 10);
        List<PostResponse> expected =
                List.of(
                        PostResponse.builder()
                                .id(2L)
                                .title("popular")
                                .content("content")
                                .authorId(1L)
                                .author("author")
                                .publicStatus(true)
                                .likeCount(3L)
                                .profileImageUrl("/images/profiles/basic_profile_image.png")
                                .build());

        when(postRepository.findPostResponsesByDateLike(any(), eq(pageable))).thenReturn(expected);

        List<PostResponse> result = postService.getPosts(PostSortType.WEEKLY_LIKE, pageable);

        assertThat(result).isEqualTo(expected);
        verify(postRepository).findPostResponsesByDateLike(any(), eq(pageable));
        verify(postRepository, never()).findPostResponsesByLatest(any());
    }

    @Test
    void addLikeShouldThrowWhenDuplicateLikeExists() {
        Post post = post(9L, user(1L, "a"), category(1L, "c", user(1L, "a"), 1L), true);

        when(postRepository.findById(9L)).thenReturn(Optional.of(post));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L, "reader")));
        when(likeRepository.save(any(Like.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThatThrownBy(() -> postService.addLike(9L, 2L))
                .isInstanceOf(AlreadyAddException.class)
                .hasMessageContaining("이미 처리된 요청");
    }

    @Test
    void getLikeUserListShouldReturnProjectionForAuthor() {
        User author = user(1L, "author");
        Post post = post(10L, author, category(1L, "likes", author, 1L), true);
        List<LikeUserResponse> expected =
                List.of(
                        LikeUserResponse.builder()
                                .userId(2L)
                                .username("reader")
                                .profileImageUrl("/images/profiles/basic_profile_image.png")
                                .build());

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(likeRepository.findLikeUserResponsesByPostId(10L)).thenReturn(expected);

        List<LikeUserResponse> responses = postService.getLikeUserList(10L, 1L);

        assertThat(responses).isEqualTo(expected);
    }

    @Test
    void getLikeUserListShouldRejectDifferentUser() {
        User author = user(1L, "author");
        Post post = post(11L, author, category(1L, "likes", author, 1L), true);

        when(postRepository.findById(11L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.getLikeUserList(11L, 2L))
                .isInstanceOf(com.blog.backend.exception.LoginUserNotMatchException.class);

        verify(likeRepository, never()).findLikeUserResponsesByPostId(11L);
    }

    @Test
    void getPostCommentsShouldRejectPrivatePostForAnotherUser() {
        User author = user(1L, "author");
        Post privatePost = post(20L, author, category(10L, "secret", author, 1L), false);

        when(postRepository.findById(20L)).thenReturn(Optional.of(privatePost));

        assertThatThrownBy(() -> postService.getPostComments(20L, 2L))
                .isInstanceOf(AuthorOnlyException.class)
                .hasMessageContaining("작성자만 접근");

        verify(commentRepository, never()).findCommentResponsesByPostId(20L);
    }

    @Test
    void getPostCommentsShouldAllowAuthorToViewPrivatePostComments() {
        User author = user(1L, "author");
        Post privatePost = post(21L, author, category(10L, "secret", author, 1L), false);
        CommentResponse comment =
                CommentResponse.builder()
                        .commentId(1L)
                        .profileImageUrl("/images/profiles/basic_profile_image.png")
                        .author("author")
                        .authorId(1L)
                        .content("hello")
                        .build();

        when(postRepository.findById(21L)).thenReturn(Optional.of(privatePost));
        when(commentRepository.findCommentResponsesByPostId(21L))
                .thenReturn(java.util.List.of(comment));

        java.util.List<CommentResponse> responses = postService.getPostComments(21L, 1L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).content()).isEqualTo("hello");
    }

    @Test
    void addCommentShouldRejectPrivatePostForAnotherUser() {
        User author = user(1L, "author");
        User reader = user(2L, "reader");
        Post privatePost = post(30L, author, category(10L, "secret", author, 1L), false);

        when(userRepository.findById(2L)).thenReturn(Optional.of(reader));
        when(postRepository.findById(30L)).thenReturn(Optional.of(privatePost));

        assertThatThrownBy(() -> postService.addComment(30L, new AddCommentRequest("blocked"), 2L))
                .isInstanceOf(AuthorOnlyException.class)
                .hasMessageContaining("작성자만 접근");

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addCommentShouldAllowAuthorOnPrivatePost() {
        User author = user(1L, "author");
        Post privatePost = post(31L, author, category(10L, "secret", author, 1L), false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(postRepository.findById(31L)).thenReturn(Optional.of(privatePost));

        CommentResponse response = postService.addComment(31L, new AddCommentRequest("mine"), 1L);

        assertThat(response.authorId()).isEqualTo(1L);
        assertThat(response.content()).isEqualTo("mine");
    }

    @Test
    void addLikeShouldRejectPrivatePostForAnotherUser() {
        User author = user(1L, "author");
        User reader = user(2L, "reader");
        Post privatePost = post(40L, author, category(10L, "secret", author, 1L), false);

        when(postRepository.findById(40L)).thenReturn(Optional.of(privatePost));
        when(userRepository.findById(2L)).thenReturn(Optional.of(reader));

        assertThatThrownBy(() -> postService.addLike(40L, 2L))
                .isInstanceOf(AuthorOnlyException.class)
                .hasMessageContaining("작성자만 접근");

        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void addLikeShouldAllowAuthorOnPrivatePost() {
        User author = user(1L, "author");
        Post privatePost = post(41L, author, category(10L, "secret", author, 1L), false);

        when(postRepository.findById(41L)).thenReturn(Optional.of(privatePost));
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(likeRepository.countByPost(privatePost)).thenReturn(1L);

        LikeResponse response = postService.addLike(41L, 1L);

        assertThat(response.totalCount()).isEqualTo(1L);
    }

    private User user(Long id, String username) {
        return User.builder()
                .id(id)
                .username(username)
                .email(username + "@test.com")
                .password("pw")
                .profileImageUrl("/images/profiles/basic_profile_image.png")
                .build();
    }

    private Category category(Long id, String name, User user, Long count) {
        return Category.builder().id(id).name(name).user(user).count(count).build();
    }

    private Post post(Long id, User user, Category category, boolean publicStatus) {
        return Post.builder()
                .id(id)
                .user(user)
                .category(category)
                .title("title")
                .content("content")
                .publicStatus(publicStatus)
                .build();
    }
}
