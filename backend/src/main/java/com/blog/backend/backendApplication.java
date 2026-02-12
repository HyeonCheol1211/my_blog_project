package com.blog.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @SpringBootApplication 어노테이션은 다음 세 가지 역할을 동시에 수행합니다:
 * 1. @Configuration: 현재 클래스가 스프링의 설정 클래스임을 선언합니다.
 * 2. @EnableAutoConfiguration: 스프링 부트의 자동 설정을 활성화합니다.
 * 3. @ComponentScan: 현재 패키지와 하위 패키지에서 @Component, @Service, @Controller 등을 찾아 빈(Bean)으로 등록합니다.
 */
@SpringBootApplication
public class backendApplication {

    public static void main(String[] args) {
        // 이 한 줄로 전체 스프링 부트 애플리케이션이 실행됩니다.
        SpringApplication.run(backendApplication.class, args);
    }

}