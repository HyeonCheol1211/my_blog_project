package com.blog.backend.service;

import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.FollowRepository;
import com.blog.backend.domain.repository.LikeRepository;
import com.blog.backend.domain.repository.PostRepository;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.*;
import com.blog.backend.exception.DuplicateEmailException;
import com.blog.backend.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserDetailsService userDetailsService;

    @Value("${file.dir}")
    private String fileDir;

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public ProfileBasicResponse getProfileBasic(Long userId) {

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User ID", userId.toString()));

        Long followingCount = followRepository.countByFollower(user);
        Long followerCount = followRepository.countByFollowing(user);
        return ProfileBasicResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImage())
                .createdAt(user.getCreatedAt())
                .followerCount(followerCount)
                .followingCount(followingCount)
                .build();
    }

    public ProfileExtraResponse getProfileExtra(Long userId, Long loginUserId) {
        boolean isFollowing = false;
        if (userId != null) {
            isFollowing = followRepository.existsByFollower_IdAndFollowing_Id(loginUserId, userId);
        }
        Long postAllCount = postRepository.countByUser_Id(userId);
        Long postPublicCount = postRepository.countByUser_IdAndPublicStatus(userId, true);

        return ProfileExtraResponse.builder()
                .isFollowing(isFollowing)
                .postAllCount(postAllCount)
                .postPublicCount(postPublicCount)
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

    @Transactional
    public UserResponse updateProfile(
            UserUpdateRequest userUpdateRequest, MultipartFile multipartFile, Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User ID", userId.toString()));

        String password = userUpdateRequest.password();
        String email = userUpdateRequest.email();
        String bio = userUpdateRequest.bio();
        String profileImage = null;

        if (email != null && userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }

        if (email != null && !userRepository.existsByEmail(email)) {
            user.updateEmail(email);
        }
        if (password != null) {
            String encodedPassword = passwordEncoder.encode(password);
            user.updatePassword(encodedPassword);
        }

        user.updateBio(bio);

        if (multipartFile != null && !multipartFile.isEmpty()) {
            if (user.canDeleteImage()) {
                deleteOldProfileImage(user.getProfileImage());
            }
            profileImage = imageValidate(multipartFile);
        }

        if (profileImage != null) {
            user.updateProfileImage(profileImage);
        }

        return UserResponse.builder().username(user.getUsername()).email(user.getEmail()).build();
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

    public List<FollowerResponse> getFollowers(Long userId) {
        return followRepository.findAllByFollowing_Id(userId).stream()
                .map(
                        follow ->
                                FollowerResponse.builder()
                                        .followerId(follow.getFollowerId())
                                        .profileImageUrl(follow.getFollowerProfileImage())
                                        .username(follow.getFollowerUsername())
                                        .build())
                .toList();
    }

    public List<FollowingResponse> getFollowings(Long userId) {
        return followRepository.findAllByFollower_Id(userId).stream()
                .map(
                        follow ->
                                FollowingResponse.builder()
                                        .followingId(follow.getFollowingId())
                                        .profileImageUrl(follow.getFollowingProfileImage())
                                        .username(follow.getFollowingUsername())
                                        .build())
                .toList();
    }

    public List<PostResponse> getUserPosts(Long userId, Long loginUserId) {
        List<Post> posts = List.of();
        if (loginUserId != null && userId.equals(loginUserId)) {
            posts = postRepository.findAllByUser_Id(userId);
        }

        if (loginUserId == null || !userId.equals(loginUserId)) {
            posts = postRepository.findAllByUser_IdAndPublicStatus(userId, true);
        }

        return posts.stream()
                .map(
                        p ->
                                PostResponse.builder()
                                        .id(p.getId())
                                        .title(p.getTitle())
                                        .content(p.getContent())
                                        .authorId(p.getUserId())
                                        .author(p.getUsername())
                                        .publicStatus(p.isPublicStatus())
                                        .createdAt(p.getCreatedAt())
                                        .likeCount(likeRepository.countByPost(p))
                                        .build())
                .toList();
    }
}
