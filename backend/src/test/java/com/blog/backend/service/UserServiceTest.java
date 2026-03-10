package com.blog.backend.service;

import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.UserLoginRequest;
import com.blog.backend.exception.PasswordNotCorrectException;
import com.blog.backend.utils.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("성공: 올바른 비밀번호로 로그인 시 토큰을 반환한다")
    void login_Success() {
        // given
        String username = "testUser";
        String password = "rawPassword";
        String encodedPassword = "encodedPassword";
        User user = User.builder().id(1L).username(username).password(encodedPassword).build();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, encodedPassword)).willReturn(true);
        given(jwtUtil.createToken(username)).willReturn("mock-token");

        UserLoginRequest request = new UserLoginRequest(username, password);

        // when
        var response = userService.login(request);

        // then
        assertThat(response.token()).isEqualTo("mock-token");
        assertThat(response.userId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("실패: 비밀번호가 틀리면 PasswordNotCorrectException이 발생한다")
    void login_Fail_Password() {
        // given
        User user = User.builder().username("user").password("encoded").build();
        given(userRepository.findByUsername("user")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrong", "encoded")).willReturn(false);

        UserLoginRequest request = new UserLoginRequest("user", "wrong");

        // when & then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(PasswordNotCorrectException.class);
    }
}