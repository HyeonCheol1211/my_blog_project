package com.blog.backend.dto;

import com.blog.backend.domain.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


@Builder
public record UserJoinRequest (
    @NotBlank(message = "사용자이름 입력은 필수입니다.")
    String username,
    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    String password,
    String email,
    String profileImage,
    String bio
    ){
    public User toEntity(){
        return User.builder()
                .email(email)
                .password(password)
                .username(username)
                .profileImage((profileImage != null && !profileImage.isEmpty())
                        ? profileImage
                        : "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png")
                .bio(bio)
                .build();
    }
}
