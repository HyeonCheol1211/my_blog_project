package com.blog.backend.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.blog.backend.domain.Follow;
import com.blog.backend.domain.User;
import com.blog.backend.dto.FollowerResponse;
import com.blog.backend.dto.FollowingResponse;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Long countByFollower(User follower);

    Long countByFollowing(User following);

    boolean existsByFollowerAndFollowing(User follower, User following);

    void deleteByFollowerAndFollowing(User follower, User following);

    boolean existsByFollower_IdAndFollowing_Id(Long followerId, Long followingId);

    @Query(
            """
SELECT new com.blog.backend.dto.FollowerResponse(
    u.id,
    u.username,
    u.profileImageUrl
)
FROM Follow f
JOIN f.follower u
WHERE f.following.id = :userId
""")
    List<FollowerResponse> findFollowerResponsesByFollowingId(@Param("userId") Long userId);

    @Query(
            """
SELECT new com.blog.backend.dto.FollowingResponse(
    u.id,
    u.username,
    u.profileImageUrl
)
FROM Follow f
JOIN f.following u
WHERE f.follower.id = :userId
""")
    List<FollowingResponse> findFollowingResponsesByFollowerId(@Param("userId") Long userId);
}
