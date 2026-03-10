package com.blog.backend.service;

import com.blog.backend.domain.Follow;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.FollowRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.FollowResponse;
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
    public FollowResponse addFollow(Long userId, String followerUsername) {
        User following = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("User ID", userId.toString()));
        User follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(()-> new UserNotFoundException("Username", followerUsername));

        return followRepository.findByFollowerAndFollowing(follower, following)
                .map(follow -> FollowResponse.builder()
                        .followerUsername(follow.getFollower().getUsername())
                        .followingUsername(follow.getFollowing().getUsername())
                        .build())
                .orElseGet(()->{
                    Follow follow = Follow.builder()
                            .follower(follower)
                            .following(following)
                            .build();
                    if(!follower.getId().equals(following.getId())) {
                        followRepository.save(follow);
                    }
                    return FollowResponse.builder()
                            .followerUsername(follower.getUsername())
                            .followingUsername(following.getUsername())
                            .build();
                });
    }

    @Transactional
    public FollowResponse deleteFollow(Long userId, String followerUsername){
        User following = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("User ID", userId.toString()));

        User follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(()-> new UserNotFoundException("Username", followerUsername));

        return followRepository.findByFollowerAndFollowing(follower, following)
                .map(follow -> {
                    FollowResponse followResponse = FollowResponse.builder()
                            .followerUsername(follow.getFollower().getUsername())
                            .followingUsername(follow.getFollower().getUsername())
                            .build();
                    followRepository.delete(follow);
                    return followResponse;

                })
                .orElseGet(()->
                    FollowResponse.builder()
                            .followerUsername(follower.getUsername())
                            .followingUsername(following.getUsername())
                            .build()
                );
    }

}
