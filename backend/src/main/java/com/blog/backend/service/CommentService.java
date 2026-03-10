package com.blog.backend.service;

import com.blog.backend.domain.Comment;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CommentRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.AddCommentRequest;
import com.blog.backend.dto.CommentDetailResponse;
import com.blog.backend.dto.CommentResponse;
import com.blog.backend.dto.UpdateCommentRequest;
import com.blog.backend.exception.AuthorOnlyException;
import com.blog.backend.exception.CommentNotFoundException;
import com.blog.backend.exception.PostNotFoundException;
import com.blog.backend.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponse addComment(Long postId, AddCommentRequest addCommentRequest, String username) {
        String content = addCommentRequest.content();

        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException("Username", username));

        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException(postId));

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .content(content)
                .build();

        commentRepository.save(comment);

        return CommentResponse.builder()
                .commentId(comment.getId())
                .postId(postId)
                .author(username)
                .content(content)
                .build();
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, UpdateCommentRequest updateCommentRequest, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new CommentNotFoundException(commentId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException("Username", username));

        if(!comment.getUser().getId().equals(user.getId())){
            throw new AuthorOnlyException(comment.getUser().getId());
        }

        comment.updateComment(updateCommentRequest.content());
        return CommentResponse.builder()
                .author(comment.getUser().getUsername())
                .commentId(comment.getId())
                .content(comment.getContent())
                .postId(comment.getPost().getId())
                .build();
    }

    public CommentResponse deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new CommentNotFoundException(commentId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException("Username", username));

        if(!comment.getUser().getId().equals(user.getId())){
            throw new AuthorOnlyException(comment.getUser().getId());
        }

        CommentResponse commentResponse = CommentResponse.builder()
                .commentId(commentId)
                .author(comment.getUser().getUsername())
                .content(comment.getContent())
                .postId(comment.getPost().getId())
                .build();

        commentRepository.deleteById(commentId);

        return commentResponse;
    }

    public List<CommentDetailResponse> getComments(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User", username));

        List<Comment> comments = commentRepository.findAllByUser(user);
        return comments.stream()
                .map(c-> CommentDetailResponse.builder()
                                .commentId(c.getId())
                                .author(c.getUser().getUsername())
                                .postId(c.getPost().getId())
                                .postTitle(c.getPost().getTitle())
                                .content(c.getContent())
                                .build()
                        )
                .toList();
    }
}
