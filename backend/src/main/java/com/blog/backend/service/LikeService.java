package com.blog.backend.service;

import com.blog.backend.domain.Like;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.LikeRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.LikeResponse;
import com.blog.backend.dto.LikeUserResponse;
import com.blog.backend.exception.AuthorOnlyException;
import com.blog.backend.exception.PostNotFoundException;
import com.blog.backend.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;


    @Transactional
    public LikeResponse addLike(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException(postId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException(username));

        return likeRepository.findByUserAndPost(user, post)
                .map(like->LikeResponse.builder()
                                .postId(like.getPost().getId())
                                .username(like.getUser().getUsername())
                                .build()
                        )
                .orElseGet(()->{
                    Like like = Like.builder()
                            .user(user)
                            .post(post)
                            .build();

                    likeRepository.save(like);

                    return LikeResponse.builder()
                            .username(like.getUser().getUsername())
                            .postId(like.getPost().getId())
                            .build();
                });
    }

    @Transactional
    public LikeResponse deleteLike(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException(postId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException(username));

        likeRepository.findByUserAndPost(user, post)
                .ifPresent(likeRepository::delete);

        return LikeResponse.builder()
                .postId(postId)
                .username(username)
                .build();

    }

    public List<LikeUserResponse> getLikeUserList(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException(postId));

        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException(username));

        if(!post.getUser().getId().equals(user.getId())){
            throw new AuthorOnlyException(post.getUser().getId());
        }

        return likeRepository.findAllByPost(post)
                .stream()
                .map(like -> LikeUserResponse.builder()
                                .profileImageUrl(like.getUser().getProfileImage())
                                .username(like.getUser().getUsername())
                                .build()
                        )
                .toList();
    }
}
