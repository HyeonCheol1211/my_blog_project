package com.blog.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CategoryRepository;
import com.blog.backend.domain.repository.LikeRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.dto.PostResponse;
import com.blog.backend.exception.CategoryNotFoundException;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private PostRepository postRepository;
    @Mock private LikeRepository likeRepository;

    @InjectMocks private CategoryService categoryService;

    @Test
    void getCategoryPostsShouldReturnAllPostsForOwner() {
        User user = user(1L, "author");
        Category category = category(1L, "daily", user, 2L);
        Post publicPost = post(1L, user, category, true);
        Post privatePost = post(2L, user, category, false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(postRepository.findAllByCategory_Id(1L)).thenReturn(List.of(publicPost, privatePost));
        when(likeRepository.countByPost(publicPost)).thenReturn(2L);
        when(likeRepository.countByPost(privatePost)).thenReturn(0L);

        List<PostResponse> responses = categoryService.getCategoryPosts(1L, 1L);

        assertThat(responses).hasSize(2);
    }

    @Test
    void getCategoryPostsShouldReturnOnlyPublicPostsForVisitor() {
        User user = user(1L, "author");
        Category category = category(1L, "daily", user, 2L);
        Post publicPost = post(1L, user, category, true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(postRepository.findAllByCategory_IdAndPublicStatus(1L, true))
                .thenReturn(List.of(publicPost));
        when(likeRepository.countByPost(publicPost)).thenReturn(2L);

        List<PostResponse> responses = categoryService.getCategoryPosts(1L, 99L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).publicStatus()).isTrue();
    }

    @Test
    void getCategoryPostsShouldThrowWhenCategoryMissing() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryPosts(1L, 1L))
                .isInstanceOf(CategoryNotFoundException.class);
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

    private Category category(Long id, String name, User user, Long count) {
        return Category.builder().id(id).name(name).user(user).count(count).build();
    }

    private Post post(Long id, User user, Category category, boolean publicStatus) {
        return Post.builder()
                .id(id)
                .user(user)
                .category(category)
                .title("title-" + id)
                .content("content-" + id)
                .publicStatus(publicStatus)
                .build();
    }
}
