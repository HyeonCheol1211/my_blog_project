package com.blog.backend.service;

import com.blog.backend.domain.Post;
import com.blog.backend.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;

    public List<Post> getPosts(){
        return postRepository.findAll();
    }

}
