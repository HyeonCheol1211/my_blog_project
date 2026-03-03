package com.blog.backend.service;

import com.blog.backend.domain.Comment;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CommentRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.AddCommentRequest;
import com.blog.backend.dto.AddCommentResponse;
import com.blog.backend.exception.PostNotFoundException;
import com.blog.backend.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public AddCommentResponse addComment(AddCommentRequest addCommentRequest, Long postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException(username));
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException(postId));
        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .content(addCommentRequest.content())
                .build();

        commentRepository.save(comment);

        return AddCommentResponse.builder()
                .postId(post.getId())
                .author(username)
                .content(comment.getContent())
                .build();
    }
}
