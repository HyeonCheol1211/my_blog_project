package com.blog.backend.domain.repository;

import com.blog.backend.domain.Like;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    public void deleteByPost(Post post);

    public Long countById(Long id);

    public Long countByPost(Post post);
    public Optional<Like> findByUserAndPost(User user, Post post);
}
