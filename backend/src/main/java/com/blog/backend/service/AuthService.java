package com.blog.backend.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.LoginResponse;
import com.blog.backend.dto.UserLoginRequest;
import com.blog.backend.dto.UserResponse;
import com.blog.backend.dto.UserSignupRequest;
import com.blog.backend.exception.DuplicateEmailException;
import com.blog.backend.exception.DuplicateUsernameException;
import com.blog.backend.exception.PasswordNotCorrectException;
import com.blog.backend.exception.UserNotFoundException;
import com.blog.backend.utils.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    @Value("${file.dir}")
    private String fileDir;

    @Transactional
    public UserResponse signup(UserSignupRequest userSignupRequest, MultipartFile multipartFile) {
        String username = userSignupRequest.username();
        String email = userSignupRequest.email();

        checkUsername(username);
        checkEmail(email);

        String profileImage = imageValidate(multipartFile);

        String encodedPassword = passwordEncoder.encode(userSignupRequest.password());
        User user =
                User.builder()
                        .email(userSignupRequest.email())
                        .password(encodedPassword)
                        .username(userSignupRequest.username())
                        .profileImage(
                                (profileImage == null)
                                        ? "/images/profiles/basic_profile_image.png"
                                        : profileImage)
                        .bio(userSignupRequest.bio())
                        .build();

        userRepository.save(user);

        return UserResponse.builder().username(user.getUsername()).email(user.getEmail()).build();
    }

    public LoginResponse login(UserLoginRequest userLoginRequest) {
        String username = userLoginRequest.username();
        String password = userLoginRequest.password();
        User selectedUser =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException("Username", username));

        if (!passwordEncoder.matches(password, selectedUser.getPassword())) {
            throw new PasswordNotCorrectException(password);
        }
        Long userId = selectedUser.getId();

        return LoginResponse.builder()
                .token(jwtUtil.createToken(userId))
                .userId(selectedUser.getId())
                .build();
    }

    private String imageValidate(MultipartFile multipartFile) {
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String contentType = multipartFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
            }
            String originalFilename = multipartFile.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String savedFilename = UUID.randomUUID() + extension;

            String fullPath = fileDir + savedFilename;

            File folder = new File(fileDir);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            try {
                multipartFile.transferTo(new File(fullPath));
            } catch (IOException e) {
                throw new RuntimeException("프로필 이미지 저장 실패");
            }

            return "/images/profiles/" + savedFilename;
        }
        return null;
    }

    public void checkUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException(username);
        }
    }

    public void checkEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }
    }
}
