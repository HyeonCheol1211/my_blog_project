package com.blog.backend.service;

import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.ProfileResponse;
import com.blog.backend.dto.UserJoinRequest;
import com.blog.backend.dto.UserResponse;
import com.blog.backend.exception.DuplicateEmailException;
import com.blog.backend.exception.DuplicateUsernameException;
import com.blog.backend.exception.PasswordNotCorrectException;
import com.blog.backend.exception.UserNotFoundException;
import com.blog.backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    @Value("${file.dir}")
    private String fileDir;

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserResponse join(UserJoinRequest userJoinRequest, MultipartFile multipartFile){
        userRepository.findByUsername(userJoinRequest.username())
                .ifPresent(u->{
                    throw new DuplicateUsernameException(u.getUsername());
                });
        userRepository.findByEmail(userJoinRequest.email())
                .ifPresent(u->{
                    throw new DuplicateEmailException(u.getEmail());
                });

        String profileImage = imageValidate(multipartFile);

        User user = User.builder()
                .email(userJoinRequest.email())
                .password(userJoinRequest.password())
                .username(userJoinRequest.username())
                .profileImage((profileImage != null)
                        ? profileImage
                        : "/images/profiles/basic_profile_image.png")
                .bio(userJoinRequest.bio())
                .build();

        userRepository.save(user);

        return UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public String login(String username, String password){
        User selectedUser = userRepository.findByUsername(username)
                .orElseThrow(()->new UserNotFoundException("존재하지 않는 유저입니다."));

        if(!selectedUser.getPassword().equals(password)){
            throw new PasswordNotCorrectException(password);
        }

        return jwtUtil.createToken(username);
    }

    public ProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("null"));
        return ProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImage())
                .createdAt(user.getCreatedAt())
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
            String savedFilename = UUID.randomUUID().toString() + extension;

            String fullPath = fileDir + savedFilename;

            File folder = new File(fileDir);
            if(!folder.exists()){
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


}
