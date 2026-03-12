package com.blog.backend.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blog.backend.domain.Follow;
import com.blog.backend.domain.User;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Long countByFollower(User follower);

    Long countByFollowing(User following);

    boolean existsByFollowerAndFollowing(User follower, User following);

    void deleteByFollowerAndFollowing(User follower, User following);

    boolean existsByFollower_IdAndFollowing_Id(Long followerId, Long followingId);

    List<Follow> findAllByFollowing_Id(Long userId);

    List<Follow> findAllByFollower_Id(Long userId);
}
