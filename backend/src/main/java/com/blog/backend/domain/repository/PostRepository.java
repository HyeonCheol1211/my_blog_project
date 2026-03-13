package com.blog.backend.domain.repository;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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

    @Query(
            "SELECT p FROM Post p "
                    + "JOIN Like l ON l.post.id = p.id "
                    + "WHERE p.publicStatus = true "
                    + "AND l.createdAt >= :startDate "
                    + "GROUP BY p.id "
                    + "ORDER BY COUNT(l.id) DESC ")
    List<Post> findPublicPostsOrderByPeriodLike(
            @Param("startDate") LocalDateTime startDate, Pageable pageable);
}
