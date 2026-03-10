package com.blog.backend.service;

import com.blog.backend.domain.*;
import com.blog.backend.domain.repository.*;
import com.blog.backend.dto.*;
import com.blog.backend.exception.*;
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
    public PostResponse addPost(AddPostRequest addPostRequest, Long userId){
        String categoryName = addPostRequest.categoryName();
        User user = userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException("User ID", userId.toString()));
        Category category = categoryRepository.findByNameAndUser_Id(categoryName, userId)
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
                .authorId(post.getUserId())
                .author(post.getUsername())
                .categoryName(post.getCategoryName())
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
    public void deletePost(Long postId, Long userId){
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException(postId));
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("User ID", userId.toString()));

        if(!user.getId().equals(post.getUserId())){
            throw new AuthorOnlyException(post.getUserId());
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
    }



    @Transactional
    public PostResponse updatePost(Long postId, UpdatePostRequest updatePostRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new UserNotFoundException("User ID", userId.toString()));

        Post post = postRepository.findById(postId)
                .orElseThrow(()->new PostNotFoundException(postId));

        if(!userId.equals(post.getUserId())){
            throw new AuthorOnlyException(post.getUserId());
        }

        String title = updatePostRequest.title();
        String content = updatePostRequest.content();
        String categoryName = updatePostRequest.categoryName();
        boolean publicStatus = updatePostRequest.publicStatus();
        Category category = post.getCategory();
        //카테고리가 바뀐 경우 따져줘야함
        if(!post.getCategoryName().equals(categoryName)) {//바뀜
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
                .title(title)
                .content(content)
                .authorId(userId)
                .author(user.getUsername())
                .categoryName(category.getName())
                .publicStatus(publicStatus)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likeCount(likeCount)
                .build();
    }

    public PostDetailResponse getPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException(postId));
        User author = post.getUser();

        if(canGetPost(post, userId)){
            Long likeCount = likeRepository.countByPost(post);
            boolean liked = false;
            if(userId != 0L){
                User user = userRepository.findById(userId)
                        .orElseThrow(()-> new UserNotFoundException("User ID", userId.toString()));
                liked = likeRepository.existsByUserAndPost(user, post);
            }

            return PostDetailResponse.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .authorId(post.getUserId())
                    .author(post.getUsername())
                    .categoryName(post.getCategoryName())
                    .publicStatus(post.isPublicStatus())
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .likeCount(likeCount)
                    .liked(liked)
                    .profileImageUrl(author.getProfileImage())
                    .build();
        }

        throw new AuthorOnlyException(post.getUserId());

    }

    private boolean canGetPost(Post post,  Long userId) {
        if(post.isPublicStatus()){
            return true;
        }
        if(userId == 0L){
            return false;
        }

        return userId.equals(post.getUserId());
    }

    public List<PostResponse> getPosts() {
        List<Post> posts =  postRepository.findAllByPublicStatusTrue();

        return posts.stream()
                .map(p-> PostResponse.builder()
                        .id(p.getId())
                        .title(p.getTitle())
                        .content(p.getContent())
                        .authorId(p.getUserId())
                        .author(p.getUsername())
                        .categoryName(p.getCategoryName())
                        .publicStatus(p.isPublicStatus())
                        .createdAt(p.getCreatedAt())
                        .updatedAt(p.getUpdatedAt())
                        .likeCount(likeRepository.countByPost(p))
                        .profileImageUrl(p.getProfileImage())
                        .build()
                ).toList();
    }

    public List<LikeUserResponse> getLikeUserList(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (!post.getUserId().equals(userId)) {
            throw new LoginUserNotMatchException(post.getUserId(), userId);
        }

        return likeRepository.findAllByPost(post)
                .stream()
                .map(like -> LikeUserResponse.builder()
                        .userId(like.getUserId())
                        .profileImageUrl(like.getProfileImage())
                        .username(like.getUsername())
                        .build())
                .toList();
    }

    public List<CommentResponse> getPostComments(Long postId) {
        List<Comment> comments = commentRepository.findAllByPost_Id(postId);

        return comments.stream()
                .map(c->CommentResponse.builder()
                        .commentId(c.getId())
                        .author(c.getUsername())
                        .postId(c.getPostId())
                        .content(c.getContent())
                        .build()
                ).toList();
    }

    @Transactional
    public CommentResponse addComment(Long postId, AddCommentRequest addCommentRequest, Long userId) {
        String content = addCommentRequest.content();

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("User ID", userId.toString()));

        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException(postId));

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .content(content)
                .build();

        commentRepository.save(comment);

        return CommentResponse.builder()
                .commentId(comment.getId())
                .postId(postId)
                .author(user.getUsername())
                .content(content)
                .build();
    }

    @Transactional
    public LikeResponse addLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException(postId));
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("User ID", userId.toString()));

        if(postRepository.existsIdAndUser_Id(postId, userId)){
            throw new AlreadyAddException();
        }

        Like like = Like.builder()
                .post(post)
                .user(user)
                .build();

        likeRepository.save(like);

        return LikeResponse.builder()
                .userId(userId)
                .postId(postId)
                .build();
    }
}
