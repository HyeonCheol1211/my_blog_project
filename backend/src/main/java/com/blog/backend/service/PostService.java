package com.blog.backend.service;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.*;
import com.blog.backend.dto.AddPostRequest;
import com.blog.backend.dto.UpdatePostRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void addPost(String username, AddPostRequest addPostRequest){
        String categoryName = addPostRequest.getCategoryName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("존재하지 않는 유저라 글을 쓸 수 없습니다."));
        Category category = categoryRepository.findByNameAndUser(categoryName, user)
                .orElseGet(()->addCategory(user, categoryName));
        Post post = addPostRequest.toEntity(user, category);
        postRepository.save(post);
        category.increaseCount();
    }

    private Category addCategory(User user, String categoryName) {
        Category category = Category.builder()
                .user(user)
                .name(categoryName)
                .build();
        return categoryRepository.save(category);
    }

    @Transactional
    public void deletePost(String username, Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new RuntimeException("일치하는 글이 없습니다."));
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("유저가 존재하지 않습니다."));
        if(!user.equals(post.getUser())){
            throw new RuntimeException("글의 작성자만 삭제할 수 있습니다.");
        }
        Category category = post.getCategory();
        Long cnt = category.getCount();
        if(cnt==1) {
            categoryRepository.delete(category);
        }else{
            category.decreaseCount();
        }
        //글 삭제
        postRepository.delete(post);
    }

    public List<Post> getMyPosts(String username){
        User user = userRepository.findByUsername(username)
                        .orElseThrow(()->new RuntimeException("유저가 존재하지 않습니다."));
        return postRepository.findAllByUser(user);
    }


    @Transactional
    public void updatePost(String username, UpdatePostRequest updatePostRequest, Long postId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("유저가 존재하지 않습니다."));

        Post post = postRepository.findById(postId)
                .orElseThrow(()->new RuntimeException("해당 아이디의 게시글이 존재하지 않습니다."));

        if(!user.getId().equals(post.getUser().getId())){
            throw new RuntimeException("작성자만 수정할 수 있습니다.");
        }

        String title = updatePostRequest.getTitle();
        String content = updatePostRequest.getContent();
        String categoryName = updatePostRequest.getCategoryName();
        Boolean publicStatus = updatePostRequest.isPublicStatus();
        Category category;
        //카테고리가 바뀐 경우 따져줘야함
        if(!post.getCategory().getName().equals(categoryName)) {//바뀜
            category = categoryRepository.findByNameAndUser(updatePostRequest.getCategoryName(), user)
                    .orElseGet(() -> addCategory(user, categoryName));
            category.increaseCount();

            if(post.getCategory().getCount()==1){
                categoryRepository.delete(post.getCategory());
            }else{
                post.getCategory().decreaseCount();
            }
        }else{
            category = post.getCategory();
        }
        post.update(user, category, title, content, publicStatus);
    }
}
