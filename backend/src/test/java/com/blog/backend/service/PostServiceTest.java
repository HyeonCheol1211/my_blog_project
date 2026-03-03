package com.blog.backend.service;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.CategoryRepository;
import com.blog.backend.domain.repository.LikeRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.AddPostRequest;
import com.blog.backend.dto.DeletePostResponse;
import com.blog.backend.dto.PostResponse;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock private PostRepository postRepository;
    @Mock private UserRepository userRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private LikeRepository likeRepository;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("성공: 기존 카테고리가 존재하는 경우 게시글이 정상 등록된다.")
    void addPost() {
        String username = "testUser";
        AddPostRequest addPostRequest = AddPostRequest.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .categoryName("개발")
                .publicStatus(true)
                .build();

        User mockUser = User.builder().username(username).build();
        Category mockCategory = Category.builder().name("개발").user(mockUser).build();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(mockUser));
        given(categoryRepository.findByNameAndUser("개발", mockUser)).willReturn(Optional.empty());
        given(likeRepository.countByPost(any(Post.class))).willReturn(0L);
        given(categoryRepository.save(any(Category.class))).willReturn(mockCategory);

        PostResponse postResponse = postService.addPost(username, addPostRequest);

        assertNotNull(postResponse);
        assertEquals("테스트 제목", postResponse.title());
        assertEquals("개발", postResponse.categoryName());


        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);

        verify(postRepository).save(any(Post.class));
        verify(categoryRepository, times(1)).save(categoryCaptor.capture());

        Category savedCategory = categoryCaptor.getValue();


        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(savedCategory.getName()).isEqualTo("개발");
            softAssertions.assertThat(savedCategory.getUser().getUsername()).isEqualTo(mockUser.getUsername());
            softAssertions.assertThat(mockCategory.getCount()).isEqualTo(1L);
        });
    }

    @Test
    @DisplayName("성공: 본인의 게시글을 정상적으로 삭제한다 (카테고리 글이 1개라 카테고리도 삭제됨)")
    void deletePost_Success() {
        // Given (준비)
        Long postId = 1L;
        String username = "testUser";

        User mockUser = User.builder().username(username).build();
        // 카테고리에 글이 1개(count=1)인 상황을 가정
        Category mockCategory = Category.builder().name("개발").user(mockUser).build();
        mockCategory.increaseCount();

        // mockUser가 작성한 mockPost 객체 준비
        Post mockPost = Post.builder()
                .title("지울 글").content("내용").user(mockUser).category(mockCategory).build();

        // 대본 짜주기
        given(postRepository.findById(postId)).willReturn(Optional.of(mockPost));
        given(userRepository.findByUsername(username)).willReturn(Optional.of(mockUser));

        // When (실행)
        DeletePostResponse response = postService.deletePost(username, postId);

        // Then (검증)
        // 1. 응답 메세지 검증
        assertEquals("성공적으로 삭제되었습니다.", response.message());
        assertEquals(postId, response.id());

        // 2. ★ 핵심: 삭제 메서드가 진짜로 호출되었는지 검증!
        verify(postRepository, times(1)).delete(mockPost);

        // 3. 보너스: 카테고리 글이 1개였으므로 카테고리 삭제 로직도 호출되었어야 함!
        verify(categoryRepository, times(1)).delete(mockCategory);
    }

    @Test
    @DisplayName("실패: 다른 사람의 게시글을 삭제하려고 하면 예외가 발생한다")
    void deletePost_Fail_AccessDenied() {
        // Given (준비)
        Long postId = 1L;
        String requestUsername = "hackerUser"; // 삭제를 요청한 나쁜 유저

        User author = User.builder().username("testUser").build(); // 진짜 작성자
        User hacker = User.builder().username(requestUsername).build(); // 해커

        // 작성자가 "testUser"인 게시글 준비
        Post mockPost = Post.builder().title("내 글").user(author).build();

        // 대본 짜주기
        given(postRepository.findById(postId)).willReturn(Optional.of(mockPost));
        given(userRepository.findByUsername(requestUsername)).willReturn(Optional.of(hacker));

        // When & Then (실행 및 예외 검증)
        assertThrows(AccessDeniedException.class, () -> postService.deletePost(requestUsername, postId));

        // 예외가 터지고 멈췄으므로, 삭제 기능은 절대 호출되지 않았어야 함을 강력하게 검증!
        verify(postRepository, never()).delete(any());
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void deletePost() {
    }

    @Test
    void getMyPosts() {
    }

    @Test
    void updatePost() {
    }

    @Test
    void getPost() {
    }

    @Test
    void getPosts() {
    }
}