package com.blog.backend.service;

import com.blog.backend.domain.Comment;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CommentRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.UpdateCommentRequest;
import com.blog.backend.exception.AuthorOnlyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PostRepository postRepository;
    @Mock private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    @DisplayName("성공: 작성자 본인이면 댓글 내용을 수정할 수 있다")
    void updateComment_Success() {
        // given
        Long commentId = 1L;
        String username = "author";
        User user = User.builder().id(1L).username(username).build();
        Post post = Post.builder().id(10L).build();
        Comment comment = Comment.builder().id(commentId).user(user).post(post).content("Old content").build();

        UpdateCommentRequest request = new UpdateCommentRequest("New content");

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));

        // when
        var response = commentService.updateComment(commentId, request, username);

        // then
        assertThat(response.content()).isEqualTo("New content");
        assertThat(comment.getContent()).isEqualTo("New content");
    }

    @Test
    @DisplayName("실패: 작성자가 아닌 유저가 수정 시 AuthorOnlyException이 발생한다")
    void updateComment_Fail_AuthorOnly() {
        // given
        Long commentId = 1L;
        User author = User.builder().id(1L).username("author").build();
        User stranger = User.builder().id(2L).username("stranger").build();
        Comment comment = Comment.builder().id(commentId).user(author).build();

        UpdateCommentRequest request = new UpdateCommentRequest("Hacked content");

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(userRepository.findByUsername("stranger")).willReturn(Optional.of(stranger));

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(commentId, request, "stranger"))
                .isInstanceOf(AuthorOnlyException.class);
    }
}