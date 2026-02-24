package com.blog.backend.config;

import com.blog.backend.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authorization : {}", authorization);

        // 1. 토큰이 없으면 그냥 통과 (권한 없음)
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.error("authorization이 없습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 토큰 꺼내기
        String token = authorization.split(" ")[1];

        // 3. 만료 확인
        if (jwtUtil.isExpired(token)) {
            log.error("Token이 만료되었습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // 4. 유저네임 꺼내기
        String username = jwtUtil.getUsername(token);
        log.info("userName: {}", username);

        // 5. ★ [수정됨] 권한 없이(null) 인증 객체 만들기
        // "이 사람은 'username'입니다. 권한은 딱히 없지만 신원은 확실합니다."
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, null, null); // 권한 부분 null

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken); // 문 열어줌

        filterChain.doFilter(request, response);
    }
}