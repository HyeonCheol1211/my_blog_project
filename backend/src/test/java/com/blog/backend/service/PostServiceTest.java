package com.blog.backend.service;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CategoryRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.exception.AuthorOnlyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock private PostRepository postRepository;
    @Mock private UserRepository userRepository;
    @Mock private CategoryRepository categoryRepository;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("성공: 게시글 삭제 시 해당 카테고리에 남은 글이 없으면 카테고리도 삭제된다")
    void deletePost_WithCategoryCleanup() {
        // given
        Long postId = 1L;
        String username = "author";
        User user = User.builder().id(1L).username(username).build();
        Category category = Category.builder().id(10L).name("Java").count(1L).build();
        Post post = Post.builder().id(postId).user(user).category(category).build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));

        // when
        postService.deletePost(postId, username);

        // then
        verify(postRepository).delete(post);
        verify(categoryRepository).delete(category); // count가 1이었으므로 삭제되어야 함
    }

    @Test
    @DisplayName("실패: 비공개 게시글을 작성자가 아닌 유저가 조회하면 AuthorOnlyException이 발생한다")
    void getPost_Private_AccessDenied() {
        // given
        Long postId = 1L;
        User author = User.builder().id(1L).username("author").build();
        User stranger = User.builder().id(2L).username("stranger").build();
        Post privatePost = Post.builder().id(postId).user(author).publicStatus(false).build();

        given(postRepository.findById(postId)).willReturn(Optional.of(privatePost));
        given(userRepository.findByUsername("stranger")).willReturn(Optional.of(stranger));

        // when & then
        assertThatThrownBy(() -> postService.getPost(postId, "stranger"))
                .isInstanceOf(AuthorOnlyException.class);
    }
}