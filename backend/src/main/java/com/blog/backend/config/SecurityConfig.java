package com.blog.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 테스트를 위해 CSRF 방어 잠시 해제
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/join").permitAll() // 회원가입 경로는 누구나 허용
                        .anyRequest().authenticated() // 그 외 나머지는 인증 필요
                );

        return http.build();
    }
}