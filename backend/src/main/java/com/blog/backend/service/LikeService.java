package com.blog.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blog.backend.domain.repository.LikeRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.LikeResponse;
import com.blog.backend.exception.AlreadyDeleteException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public LikeResponse deleteLike(Long postId, Long userId) {
        if (!likeRepository.existsByUser_IdAndPost_Id(userId, postId)) {
            throw new AlreadyDeleteException();
        }
        likeRepository.removeByUser_IdAndPost_Id(userId, postId);
        Long likeCount = likeRepository.countByPost_Id(postId);

        return LikeResponse.builder().totalCount(likeCount).build();
    }
}
