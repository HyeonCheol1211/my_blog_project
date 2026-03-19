package com.blog.backend.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import jakarta.servlet.ServletException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import com.blog.backend.utils.JwtUtil;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock private JwtUtil jwtUtil;

    private JwtFilter jwtFilter;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        jwtFilter = new JwtFilter(jwtUtil);
    }

    @Test
    void shouldPassThroughWhenAuthorizationHeaderMissing()
            throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/posts/1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        RecordingFilterChain chain = new RecordingFilterChain();

        jwtFilter.doFilter(request, response, chain);

        assertThat(chain.wasCalled()).isTrue();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsInvalid()
            throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/posts");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        RecordingFilterChain chain = new RecordingFilterChain();
        when(jwtUtil.validateToken("invalid-token")).thenReturn(false);

        jwtFilter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("유효하지 않거나 만료된 토큰");
        assertThat(chain.wasCalled()).isFalse();
    }

    @Test
    void shouldTreatEmptyBearerTokenAsUnauthorizedInsteadOfThrowing()
            throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/posts");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer ");
        MockHttpServletResponse response = new MockHttpServletResponse();
        RecordingFilterChain chain = new RecordingFilterChain();
        when(jwtUtil.validateToken("")).thenReturn(false);

        jwtFilter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(chain.wasCalled()).isFalse();
        verify(jwtUtil).validateToken("");
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private static final class RecordingFilterChain
            implements jakarta.servlet.FilterChain {
        private boolean called;

        @Override
        public void doFilter(
                jakarta.servlet.ServletRequest request,
                jakarta.servlet.ServletResponse response) {
            this.called = true;
        }

        boolean wasCalled() {
            return called;
        }
    }
}
