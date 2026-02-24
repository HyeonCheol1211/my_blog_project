package com.blog.backend.service;

import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.UserRepository;
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
    public Long join(User user){
        validateDuplicateUser(user);
        userRepository.save(user);
        return user.getId();
    }

    public String login(String username, String password){
        User selectedUser = userRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("존재하지 않는 유저입니다."));

        if(!selectedUser.getPassword().equals(password)){
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        return jwtUtil.createToken(username);
    }

    private void validateDuplicateUser(User user) {
        userRepository.findByEmail(user.getEmail())
                .ifPresent(u->{
                    throw new IllegalStateException("이미 존재하는 이메일입니다.");
                });

        userRepository.findByUsername(user.getUsername())
                .ifPresent(u->{
                    throw new IllegalStateException("이미 존재하는 유저네임입니다.");
                });
    }
}
