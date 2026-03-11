package com.blog.backend.domain.repository;

import com.blog.backend.domain.Like;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Long countByPost(Post post);
    Optional<Like> findByUserAndPost(User user, Post post);
    boolean existsByUserAndPost(User user, Post post);
    List<Like> findAllByPost(Post post);
    boolean existsByUser_IdAndPost_Id(Long userId, Long postId);
    void removeByUser_IdAndPost_Id(Long userId, Long postId);
}
