package com.blog.backend.service;

import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.FollowRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.ProfileResponse;
import com.blog.backend.dto.UserJoinRequest;
import com.blog.backend.dto.UserResponse;
import com.blog.backend.dto.UserUpdateRequest;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    @Value("${file.dir}")
    private String fileDir;

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;

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

    public ProfileResponse getProfile(String targetUsername, String username) {

        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(()-> new UserNotFoundException(targetUsername));

        Long followingCount = followRepository.countByUser1(targetUser);
        Long followerCount = followRepository.countByUser2(targetUser);
        Long postCount = 0L;
        if(!targetUsername.equals(username)) {
            postCount = postRepository.countByUserAndPublicStatus(targetUser, true);
        }
        if(targetUsername.equals(username)){
            postCount = postRepository.countByUser(targetUser);
        }
        boolean isFollowing = false;
        if(username != null) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(()-> new UserNotFoundException(username));
            isFollowing =followRepository.existsByUser1AndUser2(user, targetUser);
        }
        return ProfileResponse.builder()
                .id(targetUser.getId())
                .username(targetUser.getUsername())
                .email(targetUser.getEmail())
                .bio(targetUser.getBio())
                .profileImageUrl(targetUser.getProfileImage())
                .createdAt(targetUser.getCreatedAt())
                .followerCount(followerCount)
                .followingCount(followingCount)
                .postCount(postCount)
                .isFollowing(isFollowing)
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

    @Transactional
    public UserResponse updateProfile(UserUpdateRequest userUpdateRequest, MultipartFile multipartFile, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException(username));

        String password = userUpdateRequest.password();
        String email = userUpdateRequest.email();
        String bio = userUpdateRequest.bio();
        String profileImage = null;
        if(email != null) {
            userRepository.findByEmail(userUpdateRequest.email())
                    .ifPresent(u -> {
                        if(!u.getId().equals(user.getId())) {
                            throw new DuplicateEmailException(u.getEmail());
                        }
                    });
            user.updateEmail(email);
        }
        if(password != null) {
            user.updatePassword(password);
        }

        user.updateBio(bio);

        if(multipartFile != null && !multipartFile.isEmpty()){
            if(user.getProfileImage() != null && !user.getProfileImage().equals("/images/profiles/basic_profile_image.png")){
                deleteOldProfileImage(user.getProfileImage());
            }
            profileImage = imageValidate(multipartFile);
        }

        if(profileImage != null){
            user.updateProfileImage(profileImage);
        }

        return UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();

    }

    private void deleteOldProfileImage(String profileImage) {
        try {
            String fileName = profileImage.substring(profileImage.lastIndexOf("/") + 1);
            Path filePath = Paths.get(fileDir + fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("예전 프로필 사진 삭제 실패");
        }
    }
}
