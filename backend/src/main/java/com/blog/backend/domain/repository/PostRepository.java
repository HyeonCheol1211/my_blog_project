package com.blog.backend.domain.repository;

import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUser(User user);
    List<Post> findAllByPublicStatusTrue();

    List<Post> findAllByUserAndPublicStatus(User user, boolean publicStatus);

    Long countByUserAndPublicStatus(User user2, boolean b);

    Long countByUser(User targetUser);
}
