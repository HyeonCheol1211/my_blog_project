package com.blog.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // "이제부터 스프링 시큐리티 통제 들어간다!" 선언
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter; // 현철 님이 방금까지 만든 그 필터!

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF (위조 요청) 공격 방어 기능 끄기
                // (JWT를 쓸 때는 세션을 안 쓰기 때문에 꺼도 안전합니다)
                .csrf(csrf -> csrf.disable())

                // 2. ★ 우리가 아까 배운 '금붕어 기억력' 설정! (Stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3. 길 나누기 (VIP 패스 vs 일반 검사)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/join", "/api/users/login", "/api/posts/list").permitAll() // 로그인, 회원가입은 토큰 없어도 무사 통과!
                        .anyRequest().authenticated() // 그 외의 모든 경로는 "무조건 토큰 검사해!"
                )

                // 4. ★ 하이라이트: 우리 보안 요원(JwtFilter)을 정문에 배치하기!
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}