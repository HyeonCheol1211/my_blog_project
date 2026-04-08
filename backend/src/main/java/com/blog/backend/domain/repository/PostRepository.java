package com.blog.backend.domain.repository;

import com.blog.backend.domain.Post;
import com.blog.backend.dto.PostDetailResponse;
import com.blog.backend.dto.PostResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Long countByUser_Id(Long userId);

    Long countByUser_IdAndPublicStatus(Long userId, boolean b);

    @Query(
            """
            SELECT new com.blog.backend.dto.PostResponse(
                p.id,
                p.title,
                p.content,
                u.id,
                u.username,
                p.publicStatus,
                p.createdAt,
                count(DISTINCT l.id),
                u.profileImageUrl
            )
            FROM Post p
            JOIN p.user u
            LEFT JOIN Like l ON l.post = p
            WHERE u.id = :userId AND ((:loginUserId IS NOT NULL AND :loginUserId = :userId) OR p.publicStatus = true)
            GROUP BY p.id, u.id, u.username, p.title, p.content, p.publicStatus, p.createdAt, u.profileImageUrl
            ORDER BY p.createdAt DESC
            """)
    List<PostResponse> findUserPosts(
            @Param("userId") Long userId, @Param("loginUserId") Long loginUserId);

    @Query(
            """
      SELECT new com.blog.backend.dto.PostResponse(
          p.id,
          p.title,
          p.content,
          u.id,
          u.username,
          p.publicStatus,
          p.createdAt,
          COUNT(l.id),
          u.profileImageUrl
      )
      FROM Post p
      JOIN p.user u
      JOIN p.category c
      LEFT JOIN Like l ON l.post = p
      WHERE c.id = :categoryId
        AND (c.user.id = :userId OR p.publicStatus = true)
      GROUP BY p.id, u.id, c.id
      """)
    List<PostResponse> findPostResponsesByCategoryIdAndUserId(
            @Param("categoryId") Long categoryId, @Param("userId") Long userId);

    @Query(
            """
    SELECT new com.blog.backend.dto.PostDetailResponse(
        p.id,
        p.title,
        p.content,
        u.id,
        u.username,
        c.name,
        p.publicStatus,
        p.createdAt,
        p.updatedAt,
        COUNT(DISTINCT l.id),
        CASE WHEN COUNT(myLike.id) > 0 THEN true ELSE false END,
        u.profileImageUrl
    )
    FROM Post p
    JOIN p.user u
    JOIN p.category c
    LEFT JOIN Like l ON l.post = p
    LEFT JOIN Like myLike ON myLike.post = p AND myLike.user.id = :userId
    WHERE p.id = :postId
    GROUP BY p.id, u.id, c.name, myLike.id
""")
    PostDetailResponse findPostDetailResponse(
            @Param("postId") Long postId, @Param("userId") Long userId);

    @Query(
            """
      SELECT new com.blog.backend.dto.PostResponse(
          p.id,
          p.title,
          p.content,
          u.id,
          u.username,
          p.publicStatus,
          p.createdAt,
          COUNT(l.id),
          u.profileImageUrl
      )
      FROM Post p
      JOIN p.user u
      JOIN p.category c
      LEFT JOIN Like l ON l.post = p
      WHERE p.publicStatus = true
      GROUP BY p.id, u.id, c.id
      ORDER BY p.createdAt DESC
    """)
    List<PostResponse> findPostResponsesByLatest(Pageable paginationOnly);

    @Query(
            """
      SELECT new com.blog.backend.dto.PostResponse(
          p.id,
          p.title,
          p.content,
          u.id,
          u.username,
          p.publicStatus,
          p.createdAt,
          COUNT(l.id),
          u.profileImageUrl
      )
      FROM Post p
      JOIN p.user u
      JOIN p.category c
      LEFT JOIN Like l ON l.post = p AND l.createdAt >= :startDate
      WHERE p.publicStatus = true
      GROUP BY p.id, u.id, c.id
      ORDER BY COUNT(l.id) DESC
    """)
    List<PostResponse> findPostResponsesByDateLike(
            @Param("startDate") LocalDateTime startDate, Pageable paginationOnly);
}
