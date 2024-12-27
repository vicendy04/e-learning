package com.myproject.elearning.service;

import com.myproject.elearning.domain.Post;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.post.PostCreateReq;
import com.myproject.elearning.dto.request.post.PostUpdateReq;
import com.myproject.elearning.dto.response.post.PostGetRes;
import com.myproject.elearning.dto.response.post.PostListRes;
import com.myproject.elearning.dto.response.post.PostUpdateRes;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.mapper.PostMapper;
import com.myproject.elearning.repository.PostRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.service.redis.RedisPostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class PostService {
    PostRepository postRepository;
    UserRepository userRepository;
    PostMapper postMapper;
    RedisPostService redisPostService;

    // note: bi trigger select user neu PostGetRes can thong tin khac cua user
    @Transactional
    public PostGetRes addPost(Long userId, PostCreateReq request) {
        User userRef = userRepository.getReferenceIfExists(userId);

        Post post = postMapper.toEntity(request);
        post.setUser(userRef);

        return postMapper.toGetResponse(postRepository.save(post));
    }

    public PostGetRes getPost(Long id) {
        return postRepository.findPostGetResById(id).orElseThrow(() -> new InvalidIdException(id));
    }

    public PagedRes<PostListRes> getPostsByUser(Long userId, Pageable pageable) {
        Page<Post> posts = postRepository.findAllByUserId(userId, pageable);
        return PagedRes.from(posts.map(postMapper::toPostListRes));
    }

    public void toggleLike(Long postId, Long userId) {
        redisPostService.toggleLike(postId, userId);
    }

    //    public Long getPostLikes(Long postId) {
    //        return redisPostService.getLikesCount(postId);
    //    }

    @Transactional
    public void delPost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new InvalidIdException(id);
        }
        postRepository.deleteByPostId(id);
    }

    @Transactional
    public PostUpdateRes editPost(Long id, PostUpdateReq request) {
        Post post = postRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));

        postMapper.partialUpdate(post, request);
        return postMapper.toPostUpdateRes(postRepository.save(post));
    }
}
