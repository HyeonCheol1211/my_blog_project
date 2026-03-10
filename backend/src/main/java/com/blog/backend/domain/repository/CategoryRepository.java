package com.blog.backend.domain.repository;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameAndUser(String categoryName, User user);
    List<Category> findAllByUser_Id(Long userId);
    Optional<Category> findByNameAndUser_Id(String categoryName, Long userId);
}
