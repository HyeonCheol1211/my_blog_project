package com.blog.backend.domain.repository;

import com.blog.backend.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByUser_Id(Long userId);
    List<Comment> findAllByPost_Id(Long postId);
}
