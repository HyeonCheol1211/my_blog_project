package com.blog.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.Comment;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CommentRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.CommentDetailResponse;
import com.blog.backend.dto.CommentResponse;
import com.blog.backend.dto.UpdateCommentRequest;
import com.blog.backend.exception.AuthorOnlyException;
import com.blog.backend.exception.CommentNotFoundException;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PostRepository postRepository;
    @Mock private CommentRepository commentRepository;

    @InjectMocks private CommentService commentService;

    @Test
    void updateCommentShouldChangeContentForAuthor() {
        Comment comment = comment(1L, 1L, 10L, "old");
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        CommentResponse response =
                commentService.updateComment(
                        1L, UpdateCommentRequest.builder().content("new").build(), 1L);

        assertThat(response.content()).isEqualTo("new");
        assertThat(comment.getContent()).isEqualTo("new");
    }

    @Test
    void updateCommentShouldRejectDifferentUser() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment(1L, 1L, 10L, "old")));

        assertThatThrownBy(
                        () ->
                                commentService.updateComment(
                                        1L,
                                        UpdateCommentRequest.builder().content("new").build(),
                                        2L))
                .isInstanceOf(AuthorOnlyException.class);
    }

    @Test
    void deleteCommentShouldDeleteWhenAuthorMatches() {
        Comment comment = comment(1L, 1L, 10L, "old");
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, "author")));

        commentService.deleteComment(1L, 1L);

        verify(commentRepository).deleteById(1L);
    }

    @Test
    void getMyCommentsShouldMapPostTitle() {
        when(commentRepository.findAllByUser_Id(1L))
                .thenReturn(List.of(comment(1L, 1L, 10L, "hello")));

        List<CommentDetailResponse> responses = commentService.getMyComments(1L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).postTitle()).isEqualTo("post-title");
    }

    @Test
    void updateCommentShouldThrowWhenMissing() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(
                        () ->
                                commentService.updateComment(
                                        1L,
                                        UpdateCommentRequest.builder().content("new").build(),
                                        1L))
                .isInstanceOf(CommentNotFoundException.class);
    }

    private Comment comment(Long commentId, Long userId, Long postId, String content) {
        User user = user(userId, "author");
        Post post =
                Post.builder()
                        .id(postId)
                        .user(user)
                        .category(Category.builder().id(1L).name("daily").user(user).count(1L).build())
                        .title("post-title")
                        .content("post-content")
                        .publicStatus(true)
                        .build();
        Comment comment = Comment.builder().user(user).post(post).content(content).build();
        setField(comment, "id", commentId);
        return comment;
    }

    private User user(Long id, String username) {
        return User.builder()
                .id(id)
                .username(username)
                .email(username + "@test.com")
                .password("pw")
                .profileImage("/images/profiles/basic_profile_image.png")
                .build();
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
