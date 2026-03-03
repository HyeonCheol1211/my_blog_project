package com.blog.backend.domain.repository;

import com.blog.backend.domain.Comment;
import com.blog.backend.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    public void deleteByPost(Post post);

    public List<Comment> findAllByPost(Post post);
}
