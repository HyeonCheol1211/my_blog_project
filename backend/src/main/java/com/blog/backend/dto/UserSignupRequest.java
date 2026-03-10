package com.blog.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


@Builder
public record UserSignupRequest(
    @NotBlank(message = "사용자이름 입력은 필수입니다.")
    String username,
    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    String password,
    String email,
    String bio
    ){
}
