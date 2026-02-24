package com.blog.backend.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(AbstractHttpConfigurer::disable) // ui로 들어오는 것 disable
                .csrf(AbstractHttpConfigurer::disable) // csrf 보안 disable
                .cors(AbstractHttpConfigurer::disable) // cors 보안 disable

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/join", "/api/users/login").permitAll() // 로그인, 회원가입은 누구나 접근 가능
                        .requestMatchers(HttpMethod.POST, "/api/users/**").permitAll() // (선택) POST 요청 열어두기
                        .anyRequest().authenticated() // 나머지는 인증 필요
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // ★ 세션 사용 안 함 (JWT 필수)
                )
                .build();
    }
}