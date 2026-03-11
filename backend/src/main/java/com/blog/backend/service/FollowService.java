package com.blog.backend.service;

import com.blog.backend.domain.Follow;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.FollowRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.FollowResponse;
import com.blog.backend.exception.AlreadyAddException;
import com.blog.backend.exception.AlreadyDeleteException;
import com.blog.backend.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Transactional
    public FollowResponse addFollow(Long userId, Long loginUserId) {
        User following = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("User ID", userId.toString()));
        User follower = userRepository.findById(loginUserId)
                .orElseThrow(()-> new UserNotFoundException("User ID", loginUserId.toString()));

        if(followRepository.existsByFollowerAndFollowing(follower, following)){
            throw new AlreadyAddException();
        }
        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);

        return FollowResponse.builder()
                .followerUsername(follower.getUsername())
                .followingUsername(following.getUsername())
                .build();
    }

    @Transactional
    public void deleteFollow(Long userId, Long loginUserId){
        User following = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("User ID", userId.toString()));

        User follower = userRepository.findById(loginUserId)
                .orElseThrow(()-> new UserNotFoundException("User ID", loginUserId.toString()));

        if(!followRepository.existsByFollowerAndFollowing(follower, following)){
            throw new AlreadyDeleteException();
        }

        followRepository.deleteByFollowerAndFollowing(follower, following);
    }

}
