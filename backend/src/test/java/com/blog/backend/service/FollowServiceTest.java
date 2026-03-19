package com.blog.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.FollowRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.FollowResponse;
import com.blog.backend.exception.AlreadyAddException;
import com.blog.backend.exception.AlreadyDeleteException;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private FollowRepository followRepository;

    @InjectMocks private FollowService followService;

    @Test
    void addFollowShouldReturnUsernames() {
        User following = user(1L, "target");
        User follower = user(2L, "login");
        when(userRepository.findById(1L)).thenReturn(Optional.of(following));
        when(userRepository.findById(2L)).thenReturn(Optional.of(follower));
        when(followRepository.existsByFollowerAndFollowing(follower, following)).thenReturn(false);

        FollowResponse response = followService.addFollow(1L, 2L);

        assertThat(response.followingUsername()).isEqualTo("target");
        assertThat(response.followerUsername()).isEqualTo("login");
    }

    @Test
    void addFollowShouldRejectDuplicateFollow() {
        User following = user(1L, "target");
        User follower = user(2L, "login");
        when(userRepository.findById(1L)).thenReturn(Optional.of(following));
        when(userRepository.findById(2L)).thenReturn(Optional.of(follower));
        when(followRepository.existsByFollowerAndFollowing(follower, following)).thenReturn(true);

        assertThatThrownBy(() -> followService.addFollow(1L, 2L))
                .isInstanceOf(AlreadyAddException.class);
    }

    @Test
    void deleteFollowShouldRemoveExistingFollow() {
        User following = user(1L, "target");
        User follower = user(2L, "login");
        when(userRepository.findById(1L)).thenReturn(Optional.of(following));
        when(userRepository.findById(2L)).thenReturn(Optional.of(follower));
        when(followRepository.existsByFollowerAndFollowing(follower, following)).thenReturn(true);

        followService.deleteFollow(1L, 2L);

        verify(followRepository).deleteByFollowerAndFollowing(follower, following);
    }

    @Test
    void deleteFollowShouldRejectWhenAlreadyDeleted() {
        User following = user(1L, "target");
        User follower = user(2L, "login");
        when(userRepository.findById(1L)).thenReturn(Optional.of(following));
        when(userRepository.findById(2L)).thenReturn(Optional.of(follower));
        when(followRepository.existsByFollowerAndFollowing(follower, following)).thenReturn(false);

        assertThatThrownBy(() -> followService.deleteFollow(1L, 2L))
                .isInstanceOf(AlreadyDeleteException.class);
    }

    private User user(Long id, String username) {
        return User.builder()
                .id(id)
                .username(username)
                .email(username + "@test.com")
                .password("pw")
                .build();
    }
}
