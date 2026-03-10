package com.blog.backend.service;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.Post;
import com.blog.backend.domain.User;
import com.blog.backend.domain.repository.*;
import com.blog.backend.dto.*;
import com.blog.backend.exception.AuthorOnlyException;
import com.blog.backend.exception.PostNotFoundException;
import com.blog.backend.exception.UserNotFoundException;
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
    public PostResponse addPost(AddPostRequest addPostRequest, String username){
        String categoryName = addPostRequest.categoryName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new UserNotFoundException("Username", username));
        Category category = categoryRepository.findByNameAndUser(categoryName, user)
                .orElseGet(()->addCategory(user, categoryName));
        Post post = Post.builder()
                .user(user)
                .category(category)
                .title(addPostRequest.title())
                .content(addPostRequest.content())
                .publicStatus(addPostRequest.publicStatus())
                .build();
        postRepository.save(post);
        category.increaseCount();

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorId(post.getUser().getId())
                .author(post.getUser().getUsername())
                .categoryName(post.getCategory().getName())
                .publicStatus(post.isPublicStatus())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likeCount(0L)
                .build();
    }

    private Category addCategory(User user, String categoryName) {
        Category category = Category.builder()
                .user(user)
                .name(categoryName)
                .build();
        return categoryRepository.save(category);
    }

    @Transactional
    public DeletePostResponse deletePost(Long postId, String username){
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException(postId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException("Username", username));

        if(!user.getId().equals(post.getUser().getId())){
            throw new AuthorOnlyException(post.getUser().getId());
        }

        postRepository.delete(post);

        Category category = post.getCategory();
        Long cnt = category.getCount();

        if(cnt<=1) {
            categoryRepository.delete(category);
        }
        if(cnt>1){
            category.decreaseCount();
        }

        return DeletePostResponse.builder()
                .id(postId)
                .message("성공적으로 삭제되었습니다.")
                .build();
    }



    @Transactional
    public PostResponse updatePost(Long postId, UpdatePostRequest updatePostRequest, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new UserNotFoundException("Username", username));

        Post post = postRepository.findById(postId)
                .orElseThrow(()->new PostNotFoundException(postId));

        if(!user.getId().equals(post.getUser().getId())){
            throw new AuthorOnlyException(post.getUser().getId());
        }

        String title = updatePostRequest.title();
        String content = updatePostRequest.content();
        String categoryName = updatePostRequest.categoryName();
        Boolean publicStatus = updatePostRequest.publicStatus();
        Category category = post.getCategory();
        //카테고리가 바뀐 경우 따져줘야함
        if(!post.getCategory().getName().equals(categoryName)) {//바뀜
            category = categoryRepository.findByNameAndUser(updatePostRequest.categoryName(), user)
                    .orElseGet(() -> addCategory(user, categoryName));
            post.getCategory().decreaseCount();
            category.increaseCount();
        }

        if(post.getCategory().getCount()==0){
            categoryRepository.delete(post.getCategory());
        }

        post.update(category, title, content, publicStatus);

        Long likeCount = likeRepository.countByPost(post);

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorId(post.getUser().getId())
                .author(post.getUser().getUsername())
                .categoryName(post.getCategory().getName())
                .publicStatus(post.isPublicStatus())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likeCount(likeCount)
                .build();
    }

    public PostDetailResponse getPost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException(postId));
        User author = post.getUser();

        if(canGetPost(post, username)){
            Long likeCount = likeRepository.countByPost(post);
            boolean liked = false;
            if(username != null){
                User user = userRepository.findByUsername(username)
                        .orElseThrow(()-> new UserNotFoundException("Username", username));
                liked = likeRepository.existsByUserAndPost(user, post);
            }

            return PostDetailResponse.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .authorId(post.getUser().getId())
                    .author(post.getUser().getUsername())
                    .categoryName(post.getCategory().getName())
                    .publicStatus(post.isPublicStatus())
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .likeCount(likeCount)
                    .liked(liked)
                    .profileImageUrl(author.getProfileImage())
                    .build();
        }

        throw new AuthorOnlyException(post.getUser().getId());

    }

    private boolean canGetPost(Post post,  String username) {
        if(post.isPublicStatus()){
            return true;
        }
        if(username==null){
            return false;
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException("Username", username));
        if(!user.getId().equals(post.getUser().getId())){
            return false;
        }
        return true;
    }

    public List<PostResponse> getPosts() {
        List<Post> posts =  postRepository.findAllByPublicStatusTrue();

        return posts.stream()
                .map(p-> PostResponse.builder()
                        .id(p.getId())
                        .title(p.getTitle())
                        .content(p.getContent())
                        .authorId(p.getUser().getId())
                        .author(p.getUser().getUsername())
                        .categoryName(p.getCategory().getName())
                        .publicStatus(p.isPublicStatus())
                        .createdAt(p.getCreatedAt())
                        .updatedAt(p.getUpdatedAt())
                        .likeCount(likeRepository.countByPost(p))
                        .profileImageUrl(p.getUser().getProfileImage())
                        .build()
                ).toList();
    }
}
