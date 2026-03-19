package com.blog.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.LoginResponse;
import com.blog.backend.dto.UserLoginRequest;
import com.blog.backend.dto.UserResponse;
import com.blog.backend.dto.UserSignupRequest;
import com.blog.backend.exception.DuplicateEmailException;
import com.blog.backend.exception.DuplicateUsernameException;
import com.blog.backend.exception.PasswordNotCorrectException;
import com.blog.backend.utils.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private AuthService authService;

    @BeforeEach
    void setUp() throws Exception {
        Field fileDir = AuthService.class.getDeclaredField("fileDir");
        fileDir.setAccessible(true);
        fileDir.set(authService, "/tmp/");
    }

    @Test
    void signupShouldUseDefaultProfileImageWhenFileMissing() {
        UserSignupRequest request =
                UserSignupRequest.builder()
                        .username("user")
                        .password("pw")
                        .email("user@test.com")
                        .bio("bio")
                        .build();
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("user@test.com")).thenReturn(false);
        when(passwordEncoder.encode("pw")).thenReturn("encoded");

        UserResponse response = authService.signup(request, null);

        assertThat(response.username()).isEqualTo("user");
        assertThat(response.email()).isEqualTo("user@test.com");
    }

    @Test
    void signupShouldRejectDuplicateUsername() {
        when(userRepository.existsByUsername("dup")).thenReturn(true);

        assertThatThrownBy(
                        () ->
                                authService.signup(
                                        UserSignupRequest.builder()
                                                .username("dup")
                                                .password("pw")
                                                .email("dup@test.com")
                                                .build(),
                                        null))
                .isInstanceOf(DuplicateUsernameException.class);
    }

    @Test
    void signupShouldRejectNonImageFile() {
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("user@test.com")).thenReturn(false);
        MockMultipartFile file =
                new MockMultipartFile("profileImage", "bad.txt", "text/plain", "bad".getBytes());

        assertThatThrownBy(
                        () ->
                                authService.signup(
                                        UserSignupRequest.builder()
                                                .username("user")
                                                .password("pw")
                                                .email("user@test.com")
                                                .build(),
                                        file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미지 파일만 업로드");
    }

    @Test
    void loginShouldReturnTokenForMatchingPassword() {
        User user =
                User.builder()
                        .id(1L)
                        .username("user")
                        .email("user@test.com")
                        .password("encoded")
                        .build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pw", "encoded")).thenReturn(true);
        when(jwtUtil.createToken(1L)).thenReturn("token");

        LoginResponse response =
                authService.login(
                        UserLoginRequest.builder().username("user").password("pw").build());

        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.token()).isEqualTo("token");
    }

    @Test
    void loginShouldRejectWrongPassword() {
        User user =
                User.builder()
                        .id(1L)
                        .username("user")
                        .email("user@test.com")
                        .password("encoded")
                        .build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThatThrownBy(
                        () ->
                                authService.login(
                                        UserLoginRequest.builder()
                                                .username("user")
                                                .password("wrong")
                                                .build()))
                .isInstanceOf(PasswordNotCorrectException.class);
    }

    @Test
    void checkEmailShouldRejectDuplicate() {
        when(userRepository.existsByEmail("dup@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.checkEmail("dup@test.com"))
                .isInstanceOf(DuplicateEmailException.class);
    }
}
