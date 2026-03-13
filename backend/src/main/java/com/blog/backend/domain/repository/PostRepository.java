package com.blog.backend.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByPublicStatusTrue();

    Long countByUser_Id(Long userId);

    Long countByUser_IdAndPublicStatus(Long userId, boolean b);

    List<Post> findAllByUser_Id(Long userId);

    List<Post> findAllByUser_IdAndPublicStatus(Long userId, boolean b);

    List<Post> findAllByCategory_IdAndPublicStatus(Long categoryId, boolean b);

    List<Post> findAllByCategory_Id(Long categoryId);

    Long countByCategory(Category category);

    Long countByCategoryAndPublicStatus(Category category, boolean b);
}
