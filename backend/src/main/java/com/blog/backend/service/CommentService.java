package com.blog.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blog.backend.domain.Comment;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CommentRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.CommentDetailResponse;
import com.blog.backend.dto.CommentResponse;
import com.blog.backend.dto.UpdateCommentRequest;
import com.blog.backend.exception.AuthorOnlyException;
import com.blog.backend.exception.CommentNotFoundException;
import com.blog.backend.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponse updateComment(
            Long commentId, UpdateCommentRequest updateCommentRequest, Long userId) {
        Comment comment =
                commentRepository
                        .findById(commentId)
                        .orElseThrow(() -> new CommentNotFoundException(commentId));

        if (!comment.getUserId().equals(userId)) {
            throw new AuthorOnlyException(comment.getUserId());
        }

        comment.updateComment(updateCommentRequest.content());
        return CommentResponse.builder()
                .authorId(comment.getUserId())
                .profileImageUrl(comment.getProfileImage())
                .author(comment.getUsername())
                .commentId(comment.getId())
                .content(comment.getContent())
                .postId(comment.getPostId())
                .build();
    }

    public void deleteComment(Long commentId, Long userId) {
        Comment comment =
                commentRepository
                        .findById(commentId)
                        .orElseThrow(() -> new CommentNotFoundException(commentId));
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User ID", userId.toString()));

        if (!comment.getUserId().equals(user.getId())) {
            throw new AuthorOnlyException(comment.getUserId());
        }

        commentRepository.deleteById(commentId);
    }

    public List<CommentDetailResponse> getMyComments(Long userId) {
        List<Comment> comments = commentRepository.findAllByUser_Id(userId);
        return comments.stream()
                .map(
                        c ->
                                CommentDetailResponse.builder()
                                        .profileImageUrl(c.getProfileImage())
                                        .commentId(c.getId())
                                        .author(c.getUsername())
                                        .postId(c.getPostId())
                                        .postTitle(c.getPostTitle())
                                        .content(c.getContent())
                                        .build())
                .toList();
    }
}
