package com.blog.backend.service;

import com.blog.backend.domain.Follow;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.FollowRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.AddFollowRequest;
import com.blog.backend.dto.FollowResponse;
import com.blog.backend.dto.FollowerResponse;
import com.blog.backend.dto.FollowingResponse;
import com.blog.backend.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Transactional
    public FollowResponse addFollow(AddFollowRequest addFollowRequest, String username) {
        String username2 = addFollowRequest.username();
        User user2 = userRepository.findByUsername(username2)
                .orElseThrow(()-> new UserNotFoundException(username2));
        User user1 = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException(username));

        return followRepository.findByUser1AndUser2(user1, user2)
                .map(follow -> FollowResponse.builder()
                        .username1(follow.getUser1().getUsername())
                        .username2(follow.getUser2().getUsername())
                        .build())
                .orElseGet(()->{
                    Follow follow = Follow.builder()
                            .user1(user1)
                            .user2(user2)
                            .build();
                    if(!user1.getId().equals(user2.getId())) {
                        followRepository.save(follow);
                    }
                    return FollowResponse.builder()
                            .username1(user1.getUsername())
                            .username2(user2.getUsername())
                            .build();
                });
    }

    @Transactional
    public FollowResponse deleteFollow(AddFollowRequest addFollowRequest, String username){
        User user1 = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException(username));

        String username2 = addFollowRequest.username();
        User user2 = userRepository.findByUsername(username2)
                .orElseThrow(()-> new UserNotFoundException(username2));

        return followRepository.findByUser1AndUser2(user1, user2)
                .map(follow -> {
                    FollowResponse followResponse = FollowResponse.builder()
                            .username1(follow.getUser1().getUsername())
                            .username2(follow.getUser2().getUsername())
                            .build();
                    followRepository.delete(follow);
                    return followResponse;

                })
                .orElseGet(()->
                    FollowResponse.builder()
                            .username1(user1.getUsername())
                            .username2(user2.getUsername())
                            .build()
                );
    }

    public List<FollowerResponse> getFollowerList(String username) {
        User user2 = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException(username));
        return followRepository.findAllByUser2(user2)
                .stream()
                .map(follow -> FollowerResponse.builder()
                        .profileImageUrl(follow.getUser1().getProfileImage())
                        .username(follow.getUser1().getUsername())
                        .build()
                )
                .toList();
    }

    public List<FollowingResponse> getFollowingList(String username) {
        User user1 = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException(username));
        return followRepository.findAllByUser1(user1)
                .stream()
                .map(follow -> FollowingResponse.builder()
                        .profileImageUrl(follow.getUser2().getProfileImage())
                        .username(follow.getUser2().getUsername())
                        .build()
                )
                .toList();
    }
}
