package com.blog.backend.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blog.backend.domain.repository.LikeRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.exception.AlreadyDeleteException;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock private LikeRepository likeRepository;
    @Mock private UserRepository userRepository;
    @Mock private PostRepository postRepository;

    @InjectMocks private LikeService likeService;

    @Test
    void deleteLikeShouldRemoveLike() {
        when(likeRepository.existsByUser_IdAndPost_Id(2L, 1L)).thenReturn(true);

        likeService.deleteLike(1L, 2L);
        verify(likeRepository).removeByUser_IdAndPost_Id(2L, 1L);
    }

    @Test
    void deleteLikeShouldRejectWhenAlreadyDeleted() {
        when(likeRepository.existsByUser_IdAndPost_Id(2L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> likeService.deleteLike(1L, 2L))
                .isInstanceOf(AlreadyDeleteException.class);
    }
}
