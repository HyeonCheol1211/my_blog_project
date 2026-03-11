package com.blog.backend.domain.repository;

import com.blog.backend.domain.Follow;
import com.blog.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
    Long countByFollower(User follower);
    Long countByFollowing(User following);
    boolean existsByFollowerAndFollowing(User follower, User following);
    void deleteByFollowerAndFollowing(User follower, User following);
    boolean existsByFollower_IdAndFollowing_Id(Long followerId, Long followingId);
    List<Follow> findAllByFollowing_Id(Long userId);
    List<Follow> findAllByFollower_Id(Long userId);
}
