package com.blog.backend.domain.repository;

import com.blog.backend.domain.Like;
import com.blog.backend.domain.Post;
import com.blog.backend.dto.LikeUserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Long countByPost(Post post);

    @Query(
            """
            SELECT new com.blog.backend.dto.LikeUserResponse(
                u.id,
                u.username,
                u.profileImageUrl
            )
            FROM Like l
            JOIN l.user u
            WHERE l.post.id = :postId
            """)
    List<LikeUserResponse> findLikeUserResponsesByPostId(@Param("postId") Long postId);

    boolean existsByUser_IdAndPost_Id(Long userId, Long postId);

    void removeByUser_IdAndPost_Id(Long userId, Long postId);
}
