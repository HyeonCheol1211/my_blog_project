package com.blog.backend.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.blog.backend.domain.Comment;
import com.blog.backend.dto.CommentDetailResponse;
import com.blog.backend.dto.CommentResponse;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(
            """
            SELECT new com.blog.backend.dto.CommentResponse(
                c.id,
                u.profileImageUrl,
                u.username,
                u.id,
                c.content
            )
            FROM Comment c
            JOIN c.user u
            WHERE c.post.id = :postId
            """)
    List<CommentResponse> findCommentResponsesByPostId(@Param("postId") Long postId);

    @Query(
            """
        SELECT new com.blog.backend.dto.CommentDetailResponse(
            c.id,
            u.username,
            u.profileImageUrl,
            p.id,
            p.title,
            c.content
        )
        FROM Comment c
        JOIN c.user u
        JOIN c.post p
        WHERE u.id = :userId
""")
    List<CommentDetailResponse> findMyCommentDetailResponse(@Param("userId") Long userId);
}
