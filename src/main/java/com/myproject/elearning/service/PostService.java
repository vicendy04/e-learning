package com.myproject.elearning.service;

import com.myproject.elearning.domain.Post;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.post.PostCreateReq;
import com.myproject.elearning.dto.request.post.PostUpdateReq;
import com.myproject.elearning.dto.response.post.PostGetRes;
import com.myproject.elearning.dto.response.post.PostListRes;
import com.myproject.elearning.dto.response.post.PostUpdateRes;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
import com.myproject.elearning.mapper.PostMapper;
import com.myproject.elearning.repository.PostRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.security.SecurityUtils;
import com.myproject.elearning.service.redis.RedisPostService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

    @Transactional
    public Post addPost(Long userId, PostCreateReq request) {
        Post post = postMapper.toEntity(request);
        post.setUser(userRepository.getReferenceById(userId));
        return postRepository.save(post);
    }

    // Todo: có thể optimize bằng cách cho isLiked vào entity PostLike
    public PostGetRes getPost(Long postId) {
        PostGetRes postGetRes = postRepository.findPostGetResById(postId).orElseThrow(() -> new InvalidIdEx(postId));
        Optional<Long> optional = SecurityUtils.getLoginId();
        optional.ifPresent(userId -> {
            boolean liked = redisPostService.hasLiked(postId, userId);
            postGetRes.setLikedByCurrentUser(liked);
        });
        Long likesCount = redisPostService.getLikesCount(postId);
        postGetRes.setLikesCount(likesCount);
        return postGetRes;
    }

    public PagedRes<PostListRes> getPostsByUser(Long userId, Pageable pageable) {
        Page<Post> posts = postRepository.findAllByUserId(userId, pageable);
        return PagedRes.from(posts.map(postMapper::toListRes));
    }

    public void like(Long postId, Long userId) {
        redisPostService.like(postId, userId);
    }

    public void unlike(Long postId, Long userId) {
        redisPostService.unlike(postId, userId);
    }

    @Transactional
    public void delPost(Long id) {
        postRepository.deleteById(id);
    }

    @Transactional
    public PostUpdateRes editPost(Long id, PostUpdateReq request) {
        Post post = postRepository.findById(id).orElseThrow(() -> new InvalidIdEx(id));
        postMapper.partialUpdate(post, request);
        return postMapper.toUpdateRes(postRepository.save(post));
    }

    private Set<Long> filterIds(Set<Long> topPostIds, List<PostGetRes> postFromCache) {
        Set<Long> idsInCache = postFromCache.stream().map(PostGetRes::getId).collect(Collectors.toSet());
        return topPostIds.stream().filter(id -> !idsInCache.contains(id)).collect(Collectors.toSet());
    }

    public List<PostGetRes> getTopPosts(Set<Long> topPostIds) {
        // get posts from cache
        List<PostGetRes> postFromCache = redisPostService.getPosts(topPostIds);
        // filter ids which are not in cache
        Set<Long> idsNotInCache = filterIds(topPostIds, postFromCache);
        // get posts from db
        List<PostGetRes> postFromDB = postRepository.findPostGetResByIds(idsNotInCache);
        // combine
        List<PostGetRes> postGetResList =
                Stream.concat(postFromCache.stream(), postFromDB.stream()).collect(Collectors.toList());

        Long userId = SecurityUtils.getLoginId().orElse(null);
        for (PostGetRes postGetRes : postGetResList) {
            if (userId != null) {
                boolean liked = redisPostService.hasLiked(postGetRes.getId(), userId);
                postGetRes.setLikedByCurrentUser(liked);
            }
            Long likesCount = redisPostService.getLikesCount(postGetRes.getId());
            postGetRes.setLikesCount(likesCount);
        }
        return postGetResList;
    }
}
