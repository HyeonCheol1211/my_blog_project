package com.blog.backend.dto;

import com.blog.backend.domain.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class UserJoinRequest {
    @NotBlank(message = "이메일 입력은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    private String password;

    @NotBlank(message = "사용자이름 입력은 필수입니다.")
    private String username;

    private String profileImage;

    private String bio;

    public User toEntity(){
        return User.builder()
                .email(this.email)
                .password(this.password)
                .username(this.username)
                .profileImage((this.profileImage != null && !this.profileImage.isEmpty())
                        ? this.profileImage
                        : "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png")
                .bio(this.bio)
                .build();
    }
}
