package com.myproject.elearning.service;

import static com.myproject.elearning.mapper.PostMapper.POST_MAPPER;

import com.myproject.elearning.domain.Post;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.post.PostCreateReq;
import com.myproject.elearning.dto.request.post.PostUpdateReq;
import com.myproject.elearning.dto.response.post.PostGetRes;
import com.myproject.elearning.dto.response.post.PostListRes;
import com.myproject.elearning.dto.response.post.PostUpdateRes;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
import com.myproject.elearning.repository.PostLikeRepositoryCustom;
import com.myproject.elearning.repository.PostRepository;
import com.myproject.elearning.repository.UserRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
    PostLikeRepositoryCustom postLikeRepositoryCustom;
    UserRepository userRepository;

    @Transactional
    public PostGetRes addPost(Long userId, PostCreateReq request) {
        Post post = POST_MAPPER.toEntity(request);
        post.setUser(userRepository.getReferenceById(userId));
        postRepository.save(post);
        return POST_MAPPER.toGetRes(post);
    }

    @Transactional(readOnly = true)
    public PostGetRes getPost(Long postId) {
        PostGetRes postGetRes = postRepository.findPostGetResById(postId).orElseThrow(() -> new InvalidIdEx(postId));
        var likeCount = postLikeRepositoryCustom.countById(postId);
        postGetRes.setLikesCount(likeCount);
        return postGetRes;
    }

    public PagedRes<PostListRes> getPostsByUser(Long userId, Pageable pageable) {
        Page<Post> posts = postRepository.findAllByUserId(userId, pageable);
        return PagedRes.of(posts.map(POST_MAPPER::toListRes));
    }

    @Transactional
    public void delPost(Long id) {
        postRepository.deleteById(id);
    }

    @Transactional
    public PostUpdateRes editPost(Long id, PostUpdateReq request) {
        Post post = postRepository.findById(id).orElseThrow(() -> new InvalidIdEx(id));
        POST_MAPPER.partialUpdate(post, request);
        return POST_MAPPER.toUpdateRes(postRepository.save(post));
    }

    public Set<Long> filterIds(Set<Long> topPostIds, List<PostGetRes> postFromCache) {
        Set<Long> idsInCache = postFromCache.stream().map(PostGetRes::getId).collect(Collectors.toSet());
        return topPostIds.stream().filter(id -> !idsInCache.contains(id)).collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public List<PostGetRes> getTopPosts(Set<Long> idsNotInCache) {
        return postRepository.findPostGetResByIds(idsNotInCache);
    }
}
