package com.blog.backend.service;

import com.blog.backend.domain.Comment;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CommentRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.AddCommentRequest;
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
    public void addComment(AddCommentRequest addCommentRequest, Long postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("유저가 존재하지 않습니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new RuntimeException("해당 ID에 게시글이 존재하지 않습니다."));
        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .content(addCommentRequest.getContent())
                .build();

        commentRepository.save(comment);
    }
}
