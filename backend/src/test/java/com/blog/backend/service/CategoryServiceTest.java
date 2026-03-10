package com.blog.backend.service;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CategoryRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.CategoryResponse;
import com.blog.backend.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("성공: 유저의 카테고리 목록을 정확히 변환하여 반환한다")
    void getCategoryList_Success() {
        // given
        String username = "testUser";
        User user = User.builder().id(1L).username(username).build();
        Category category1 = Category.builder().name("Java").user(user).build();
        Category category2 = Category.builder().name("Spring").user(user).build();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(categoryRepository.findAllByUser(user)).willReturn(List.of(category1, category2));

        // when
        List<CategoryResponse> result = categoryService.getCategoryList(username);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).categoryName()).isEqualTo("Java");
        assertThat(result.get(1).categoryName()).isEqualTo("Spring");
    }

    @Test
    @DisplayName("실패: 존재하지 않는 유저명으로 조회 시 UserNotFoundException 발생")
    void getCategoryList_UserNotFound() {
        // given
        String username = "unknown";
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryService.getCategoryList(username))
                .isInstanceOf(UserNotFoundException.class);
    }
}