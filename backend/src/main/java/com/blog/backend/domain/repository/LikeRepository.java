package com.blog.backend.domain.repository;

import com.blog.backend.domain.Like;
import com.blog.backend.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    public void deleteByPost(Post post);

    public Long countById(Long id);

    public Long countByPost(Post post);
}
