package com.blog.backend.service;

import com.blog.backend.domain.Category;
import com.blog.backend.domain.Comment;
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
    public PostResponse addPost(String username, AddPostRequest addPostRequest){
        String categoryName = addPostRequest.categoryName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new UserNotFoundException(username));
        Category category = categoryRepository.findByNameAndUser(categoryName, user)
                .orElseGet(()->addCategory(user, categoryName));
        Post post = addPostRequest.toEntity(user, category);
        postRepository.save(post);
        category.increaseCount();

        Long likeCount = likeRepository.countByPost(post);

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getUser().getUsername())
                .categoryName(post.getCategory().getName())
                .publicStatus(post.isPublicStatus())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likeCount(likeCount)
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
    public DeletePostResponse deletePost(String username, Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException(postId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException(username));
        if(!user.equals(post.getUser())){
            throw new AuthorOnlyException(user.getId());
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

    public List<PostResponse> getMyPosts(String username){
        User user = userRepository.findByUsername(username)
                        .orElseThrow(()->new UserNotFoundException(username));
        List<Post> posts =  postRepository.findAllByUser(user);
        return posts.stream()
                .map(p-> PostResponse.builder()
                        .id(p.getId())
                        .title(p.getTitle())
                        .content(p.getContent())
                        .author(p.getUser().getUsername())
                        .categoryName(p.getCategory().getName())
                        .publicStatus(p.isPublicStatus())
                        .createdAt(p.getCreatedAt())
                        .updatedAt(p.getUpdatedAt())
                        .likeCount(likeRepository.countByPost(p))
                        .build()
                ).toList();
    }


    @Transactional
    public PostResponse updatePost(String username, UpdatePostRequest updatePostRequest, Long postId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new UserNotFoundException(username));

        Post post = postRepository.findById(postId)
                .orElseThrow(()->new PostNotFoundException(postId));

        if(!user.getId().equals(post.getUser().getId())){
            throw new AuthorOnlyException(post.getUser().getId());
        }

        String title = updatePostRequest.title();
        String content = updatePostRequest.content();
        String categoryName = updatePostRequest.categoryName();
        Boolean publicStatus = updatePostRequest.publicStatus();
        Category category;
        //카테고리가 바뀐 경우 따져줘야함
        if(!post.getCategory().getName().equals(categoryName)) {//바뀜
            category = categoryRepository.findByNameAndUser(updatePostRequest.categoryName(), user)
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
        post.update(category, title, content, publicStatus);

        Long likeCount = likeRepository.countByPost(post);

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
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

        if(!post.isPublicStatus()){
            if(username==null){
                throw new AuthorOnlyException(post.getUser().getId());
            }

            User user = userRepository.findByUsername(username)
                    .orElseThrow(()-> new UserNotFoundException(username));
            if(!user.getId().equals(post.getUser().getId())){
                throw new AuthorOnlyException(post.getUser().getId());
            }
        }

        List<Comment> comments = commentRepository.findAllByPost(post);
        Long likeCount = likeRepository.countByPost(post);

        List<CommentResponse> commentsResponse = comments.stream()
                .map(c->CommentResponse.builder()
                                .commentId(c.getId())
                                .author(c.getUser().getUsername())
                                .postId(c.getPost().getId())
                                .content(c.getContent())
                                .build()
                        ).toList();
        return PostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getUser().getUsername())
                .categoryName(post.getCategory().getName())
                .publicStatus(post.isPublicStatus())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likeCount(likeCount)
                .commentsResponse(commentsResponse)
                .build();
    }

    public List<PostResponse> getPosts() {
        List<Post> posts =  postRepository.findAllByPublicStatusTrue();

        return posts.stream()
                .map(p-> PostResponse.builder()
                        .id(p.getId())
                        .title(p.getTitle())
                        .content(p.getContent())
                        .author(p.getUser().getUsername())
                        .categoryName(p.getCategory().getName())
                        .publicStatus(p.isPublicStatus())
                        .createdAt(p.getCreatedAt())
                        .updatedAt(p.getUpdatedAt())
                        .likeCount(likeRepository.countByPost(p))
                        .build()
                ).toList();
    }
}
