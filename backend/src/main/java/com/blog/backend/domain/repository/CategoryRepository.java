package com.blog.backend.domain.repository;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.User;
import com.blog.backend.dto.CategoryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameAndUser(String categoryName, User user);

    @Query(
            """
SELECT new com.blog.backend.dto.CategoryResponse(
    c.id,
    c.name,
    COUNT(p.id)
)
FROM Category c
JOIN Post p ON p.category = c AND ((:loginUserId IS NOT NULL AND :loginUserId = :userId) OR p.publicStatus = true)
WHERE c.user.id = :userId
GROUP BY c.id, c.name
HAVING COUNT(p.id) > 0
ORDER BY c.name

""")
    List<CategoryResponse> findCategoryResponses(
            @Param("userId") Long userId, @Param("loginUserId") Long loginUserId);

    Optional<Category> findByNameAndUser_Id(String categoryName, Long userId);
}
