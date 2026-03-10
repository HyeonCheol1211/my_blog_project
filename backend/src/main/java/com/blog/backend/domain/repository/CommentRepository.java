package com.blog.backend.domain.repository;

import com.blog.backend.domain.Comment;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPost(Post post);

    List<Comment> findAllByUser(User user);

    List<Comment> findByPost(Post post);
}
