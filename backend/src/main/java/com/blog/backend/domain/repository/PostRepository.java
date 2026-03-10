package com.blog.backend.domain.repository;

import com.blog.backend.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByPublicStatusTrue();
    Long countByUser_Id(Long userId);
    Long countByUser_IdAndPublicStatus(Long userId, boolean b);
    List<Post> findAllByUser_Id(Long userId);
    List<Post> findAllByUser_IdAndPublicStatus(Long userId, boolean b);
    boolean existsIdAndUser_Id(Long postId, Long userId);
}
