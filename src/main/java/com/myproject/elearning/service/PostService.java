package com.myproject.elearning.service;

import com.myproject.elearning.domain.Post;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.post.PostCreateReq;
import com.myproject.elearning.dto.request.post.PostUpdateReq;
import com.myproject.elearning.dto.response.post.PostAddRes;
import com.myproject.elearning.dto.response.post.PostGetRes;
import com.myproject.elearning.dto.response.post.PostListRes;
import com.myproject.elearning.dto.response.post.PostUpdateRes;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.mapper.PostMapper;
import com.myproject.elearning.repository.PostLikeRepositoryCustom;
import com.myproject.elearning.repository.PostRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.security.SecurityUtils;
import com.myproject.elearning.service.redis.RedisPostService;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class PostService {
    PostRepository postRepository;
    PostLikeRepositoryCustom postLikeRepository;
    UserRepository userRepository;
    PostMapper postMapper;
    RedisPostService redisPostService;

    @Transactional
    public PostAddRes addPost(Long userId, PostCreateReq request) {
        User userRef = userRepository.getReferenceIfExists(userId);
        Post post = postMapper.toEntity(request);
        post.setUser(userRef);
        return postMapper.toPostAddRes(postRepository.save(post));
    }

    public PostGetRes getPost(Long id) {
        PostGetRes postGetRes = postRepository.findPostGetResById(id).orElseThrow(() -> new InvalidIdException(id));
        Optional<Long> optional = SecurityUtils.getLoginId();
        optional.ifPresent(userId -> {
            boolean liked = fetchLikeStatus(id, userId);
            postGetRes.setLikedByCurrentUser(liked);
        });
        return postGetRes;
    }

    /**
     * Checks Redis first, queries the database if not found, and caches the result
     */
    private boolean fetchLikeStatus(Long postId, Long userId) {
        Boolean b = redisPostService.hasLiked(postId, userId);
        if (b == null) {
            b = postLikeRepository.isPostLikedByUser(postId, userId);
            redisPostService.setLikeStatus(postId, userId, b);
        }
        return b;
    }

    public PagedRes<PostListRes> getPostsByUser(Long userId, Pageable pageable) {
        Page<Post> posts = postRepository.findAllByUserId(userId, pageable);
        return PagedRes.from(posts.map(postMapper::toPostListRes));
    }

    public void like(Long postId, Long userId) {
        redisPostService.like(postId, userId);
    }

    public void unlike(Long postId, Long userId) {
        redisPostService.unlike(postId, userId);
    }

    @Transactional
    public void delPost(Long id, Long curUserId) {
        // Todo: 1 + n problem
        Post post = postRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
        if (!curUserId.equals(post.getUser().getId())) {
            throw new AccessDeniedException("You can only delete your own posts");
        }

        postRepository.deleteByPostId(id);
    }

    @Transactional
    public PostUpdateRes editPost(Long id, PostUpdateReq request, Long curUserId) {
        // Todo: 1 + n problem
        Post post = postRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
        if (!curUserId.equals(post.getUser().getId())) {
            throw new AccessDeniedException("You can only edit your own posts");
        }

        postMapper.partialUpdate(post, request);
        return postMapper.toPostUpdateRes(postRepository.save(post));
    }
}
