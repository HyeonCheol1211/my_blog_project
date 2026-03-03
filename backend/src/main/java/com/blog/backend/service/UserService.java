package com.blog.backend.service;

import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.UserRepository;
import com.blog.backend.dto.UserJoinRequest;
import com.blog.backend.dto.UserResponse;
import com.blog.backend.exception.DuplicateEmailException;
import com.blog.backend.exception.DuplicateUsernameException;
import com.blog.backend.exception.UserNotFoundException;
import com.blog.backend.exception.PasswordNotCorrectException;
import com.blog.backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserResponse join(UserJoinRequest userJoinRequest){
        User user = userJoinRequest.toEntity();
        validateDuplicateUser(user);
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

    private void validateDuplicateUser(User user) {
        userRepository.findByUsername(user.getUsername())
                .ifPresent(u->{
                    throw new DuplicateUsernameException(u.getUsername());
                });
        userRepository.findByEmail(user.getEmail())
                .ifPresent(u->{
                    throw new DuplicateEmailException(u.getEmail());
                });

    }
}
