package com.blog.backend.domain.repository;

import com.blog.backend.domain.Follow;
import com.blog.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByUser1AndUser2(User user1, User user2);
    Long countByUser1(User user);
    Long countByUser2(User user);

    boolean existsByUser1AndUser2(User user1, User user2);

    List<Follow> findAllByUser2(User user2);

    List<Follow> findAllByUser1(User user1);
}
