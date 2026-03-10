package com.blog.backend.service;

import com.blog.backend.domain.Like;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.LikeRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.LikeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock private LikeRepository likeRepository;
    @Mock private UserRepository userRepository;
    @Mock private PostRepository postRepository;

    @InjectMocks
    private LikeService likeService;

    @Test
    @DisplayName("성공: 좋아요가 없는 상태에서 호출 시 새 좋아요가 저장된다")
    void addLike_New_Success() {
        // given
        User user = User.builder().id(1L).username("testUser").build();
        Post post = Post.builder().id(10L).build();

        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(userRepository.findByUsername("testUser")).willReturn(Optional.of(user));
        given(likeRepository.findByUserAndPost(user, post)).willReturn(Optional.empty());

        // when
        LikeResponse response = likeService.addLike(10L, "testUser");

        // then
        assertThat(response.postId()).isEqualTo(10L);
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    @DisplayName("성공: 이미 좋아요를 누른 경우 추가 저장 없이 정보를 반환한다")
    void addLike_AlreadyExists_ReturnExisting() {
        // given
        User user = User.builder().id(1L).username("testUser").build();
        Post post = Post.builder().id(10L).build();
        Like existingLike = Like.builder().user(user).post(post).build();

        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(userRepository.findByUsername("testUser")).willReturn(Optional.of(user));
        given(likeRepository.findByUserAndPost(user, post)).willReturn(Optional.of(existingLike));

        // when
        likeService.addLike(10L, "testUser");

        // then
        verify(likeRepository, never()).save(any(Like.class));
    }
}