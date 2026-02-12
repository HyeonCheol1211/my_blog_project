package com.blog.backend.domain.repository;

import com.blog.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    // 만약 중복 가입 확인이 필요하다면 이것도 자주 쓰입니다.
    boolean existsByEmail(String email);
}