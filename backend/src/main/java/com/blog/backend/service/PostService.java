package com.blog.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blog.backend.domain.*;
import com.blog.backend.domain.constant.PostSortType;
import com.blog.backend.domain.repository.*;
import com.blog.backend.dto.*;
import com.blog.backend.exception.*;

import lombok.RequiredArgsConstructor;

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
    public PostResponse addPost(AddPostRequest addPostRequest, Long userId) {
        String categoryName = addPostRequest.categoryName();
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User ID", userId.toString()));
        Category category =
                categoryRepository
                        .findByNameAndUser_Id(categoryName, userId)
                        .orElseGet(() -> addCategory(user, categoryName));
        Post post =
                Post.builder()
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
                .publicStatus(post.isPublicStatus())
                .createdAt(post.getCreatedAt())
                .likeCount(0L)
                .build();
    }

    private Category addCategory(User user, String categoryName) {
        Category category = Category.builder().user(user).name(categoryName).build();
        return categoryRepository.save(category);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post =
                postRepository
                        .findById(postId)
                        .orElseThrow(() -> new PostNotFoundException(postId));
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User ID", userId.toString()));

        if (!user.getId().equals(post.getUserId())) {
            throw new AuthorOnlyException();
        }

        postRepository.delete(post);

        Category category = post.getCategory();
        Long cnt = category.getCount();

        if (cnt <= 1) {
            categoryRepository.delete(category);
        }
        if (cnt > 1) {
            category.decreaseCount();
        }
    }

    @Transactional
    public PostResponse updatePost(Long postId, UpdatePostRequest updatePostRequest, Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User ID", userId.toString()));

        Post post =
                postRepository
                        .findById(postId)
                        .orElseThrow(() -> new PostNotFoundException(postId));

        if (!userId.equals(post.getUserId())) {
            throw new AuthorOnlyException();
        }

        String title = updatePostRequest.title();
        String content = updatePostRequest.content();
        String categoryName = updatePostRequest.categoryName();
        boolean publicStatus = updatePostRequest.publicStatus();
        Category category = post.getCategory();
        // 카테고리가 바뀐 경우 따져줘야함
        if (!post.getCategoryName().equals(categoryName)) { // 바뀜
            category =
                    categoryRepository
                            .findByNameAndUser(updatePostRequest.categoryName(), user)
                            .orElseGet(() -> addCategory(user, categoryName));
            post.getCategory().decreaseCount();
            category.increaseCount();
        }

        if (post.getCategory().getCount() == 0) {
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
                .publicStatus(publicStatus)
                .createdAt(post.getCreatedAt())
                .likeCount(likeCount)
                .build();
    }

    public PostDetailResponse getPost(Long postId, Long userId) {
        Post post =
                postRepository
                        .findById(postId)
                        .orElseThrow(() -> new PostNotFoundException(postId));
        if (!post.isPublicStatus() && (userId == null || !post.getUserId().equals(userId))) {
            throw new AuthorOnlyException();
        }

        return postRepository.findPostDetailResponse(postId, userId);
    }

    public List<PostResponse> getPosts(PostSortType postSortType, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = null;
        Pageable paginationOnly = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        if (postSortType.equals(PostSortType.LATEST)) {
            return postRepository.findPostResponsesByLatest(paginationOnly);
        }
        if (postSortType.equals(PostSortType.WEEKLY_LIKE)) {
            startDate = now.minusWeeks(1);
        }
        if (postSortType.equals(PostSortType.MONTHLY_LIKE)) {
            startDate = now.minusMonths(1);
        }
        if (postSortType.equals(PostSortType.YEARLY_LIKE)) {
            startDate = now.minusYears(1);
        }
        return postRepository.findPostResponsesByDateLike(startDate, paginationOnly);
    }

    public List<LikeUserResponse> getLikeUserList(Long postId, Long userId) {
        Post post =
                postRepository
                        .findById(postId)
                        .orElseThrow(() -> new PostNotFoundException(postId));

        if (!post.getUserId().equals(userId)) {
            throw new LoginUserNotMatchException(post.getUserId(), userId);
        }

        return likeRepository.findLikeUserResponsesByPostId(postId);
    }

    public List<CommentResponse> getPostComments(Long postId, Long userId) {
        Post post =
                postRepository
                        .findById(postId)
                        .orElseThrow(() -> new PostNotFoundException(postId));

        if (!post.isPublicStatus() && !post.getUserId().equals(userId)) {
            throw new AuthorOnlyException();
        }
        return commentRepository.findCommentResponsesByPostId(postId);
    }

    @Transactional
    public CommentResponse addComment(
            Long postId, AddCommentRequest addCommentRequest, Long userId) {
        String content = addCommentRequest.content();
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User ID", userId.toString()));

        Post post =
                postRepository
                        .findById(postId)
                        .orElseThrow(() -> new PostNotFoundException(postId));
        if (!post.isPublicStatus() && !post.getUserId().equals(userId)) {
            throw new AuthorOnlyException();
        }
        Comment comment = Comment.builder().user(user).post(post).content(content).build();

        commentRepository.save(comment);

        return CommentResponse.builder()
                .authorId(user.getId())
                .profileImageUrl(user.getProfileImageUrl())
                .commentId(comment.getId())
                .author(user.getUsername())
                .content(content)
                .build();
    }

    @Transactional
    public LikeResponse addLike(Long postId, Long userId) {
        Post post =
                postRepository
                        .findById(postId)
                        .orElseThrow(() -> new PostNotFoundException(postId));
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User ID", userId.toString()));

        if (!post.isPublicStatus() && !post.getUserId().equals(userId)) {
            throw new AuthorOnlyException();
        }

        try {
            Like like = Like.builder().post(post).user(user).build();
            likeRepository.save(like);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyAddException();
        }

        Long likeCount = likeRepository.countByPost(post);
        return LikeResponse.builder().totalCount(likeCount).build();
    }
}
