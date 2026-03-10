package com.blog.backend.service;

import com.blog.backend.domain.Follow;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.FollowRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.FollowResponse;
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
class FollowServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private FollowRepository followRepository;

    @InjectMocks
    private FollowService followService;

    @Test
    @DisplayName("성공: 다른 유저를 팔로우하면 저장 로직이 호출된다")
    void addFollow_Success() {
        // given
        User follower = User.builder().id(1L).username("follower").build();
        User following = User.builder().id(2L).username("following").build();

        given(userRepository.findById(2L)).willReturn(Optional.of(following));
        given(userRepository.findByUsername("follower")).willReturn(Optional.of(follower));
        given(followRepository.findByFollowerAndFollowing(follower, following)).willReturn(Optional.empty());

        // when
        FollowResponse response = followService.addFollow(2L, "follower");

        // then
        assertThat(response.followingUsername()).isEqualTo("following");
        verify(followRepository, times(1)).save(any(Follow.class));
    }

    @Test
    @DisplayName("실패: 자기 자신을 팔로우하려 하면 저장되지 않는다")
    void addFollow_SelfFollow_Prevented() {
        // given
        User me = User.builder().id(1L).username("me").build();

        given(userRepository.findById(1L)).willReturn(Optional.of(me));
        given(userRepository.findByUsername("me")).willReturn(Optional.of(me));
        given(followRepository.findByFollowerAndFollowing(me, me)).willReturn(Optional.empty());

        // when
        followService.addFollow(1L, "me");

        // then
        verify(followRepository, never()).save(any(Follow.class));
    }
}