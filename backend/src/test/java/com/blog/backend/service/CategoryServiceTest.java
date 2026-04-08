package com.blog.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.dto.PostResponse;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock private PostRepository postRepository;

    @InjectMocks private CategoryService categoryService;

    @Test
    void getCategoryPostsShouldReturnAllPostsForOwner() {
        List<PostResponse> expected =
                List.of(
                        PostResponse.builder()
                                .id(1L)
                                .title("title-1")
                                .content("content-1")
                                .authorId(1L)
                                .author("author")
                                .publicStatus(true)
                                .likeCount(2L)
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
        when(postRepository.findPostResponsesByCategoryIdAndUserId(1L, 1L)).thenReturn(expected);

        List<PostResponse> responses = categoryService.getCategoryPosts(1L, 1L);

        assertThat(responses).hasSize(2);
        assertThat(responses).isEqualTo(expected);
    }

    @Test
    void getCategoryPostsShouldReturnOnlyPublicPostsForVisitor() {
        List<PostResponse> expected =
                List.of(
                        PostResponse.builder()
                                .id(1L)
                                .title("title-1")
                                .content("content-1")
                                .authorId(1L)
                                .author("author")
                                .publicStatus(true)
                                .likeCount(2L)
                                .profileImageUrl("/images/profiles/basic_profile_image.png")
                                .build());
        when(postRepository.findPostResponsesByCategoryIdAndUserId(1L, 99L)).thenReturn(expected);

        List<PostResponse> responses = categoryService.getCategoryPosts(1L, 99L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).publicStatus()).isTrue();
    }

    @Test
    void getCategoryPostsShouldReturnEmptyListWhenNoVisiblePostsExist() {
        when(postRepository.findPostResponsesByCategoryIdAndUserId(1L, 1L)).thenReturn(List.of());

        List<PostResponse> responses = categoryService.getCategoryPosts(1L, 1L);

        assertThat(responses).isEmpty();
    }
}
