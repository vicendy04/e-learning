package com.myproject.elearning.service.redis;

import static com.myproject.elearning.constant.RedisKeyConstants.*;

import com.myproject.elearning.constant.RedisKeyConstants;
import com.myproject.elearning.dto.response.post.PostGetRes;
import com.myproject.elearning.repository.PostLikeRepositoryCustom;
import com.myproject.elearning.service.PostService;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RedisPostService {
    static final long DEFAULT_CACHE_DURATION = 14 * 60 * 60; // 14 hours
    static final long POST_TTL = 3 * 60 * 60;
    static final long COUNT_CACHE_TTL = 300;
    static final long MAX_RANDOM_EXPIRY = 300; // 5 min

    Random random;
    PostService postService;
    ZSetOperations<String, Object> zSetOps;
    ValueOperations<String, Object> valueOps;
    PostLikeRepositoryCustom postLikeRepository;
    HashOperations<String, String, Object> hashOps;
    RedisTemplate<String, Object> redisTemplate;

    public void like(Long postId, Long userId) {
        String key = getPostLikesKey(postId);
        hashOps.put(key, userId.toString(), LIKE_STATUS);
        redisTemplate.expire(key, DEFAULT_CACHE_DURATION, TimeUnit.SECONDS);
    }

    public void unlike(Long postId, Long userId) {
        String key = getPostLikesKey(postId);
        hashOps.put(key, userId.toString(), UNLIKE_STATUS);
        redisTemplate.expire(key, DEFAULT_CACHE_DURATION, TimeUnit.SECONDS);
    }

    public void set(Long id, PostGetRes post) {
        long randomExpiry = POST_TTL + random.nextInt((int) MAX_RANDOM_EXPIRY);
        valueOps.set(getPostKey(id), post, randomExpiry, TimeUnit.SECONDS);
    }

    public PostGetRes get(Long id) {
        Object obj = valueOps.get(getCourseKey(id));
        return obj != null ? (PostGetRes) obj : null;
    }

    public PostGetRes getAside(Long postId) {
        PostGetRes data = this.get(postId);
        if (data != null) return data;
        data = postService.getPost(postId);
        this.set(postId, data);
        return data;
    }

    public void evict(Long id) {
        redisTemplate.delete(getPostKey(id));
    }

    public void addPostToSortedSet(String postId, double score) {
        zSetOps.add(TOP_LIKE_PREFIX, postId, score);
    }

    public void removePostsExceptTopN(int n) {
        zSetOps.removeRange(TOP_LIKE_PREFIX, n, Long.MAX_VALUE);
    }

    public Set<Long> getTopPostIds(int startRank, int endRank) {
        Set<Object> range = zSetOps.range(TOP_LIKE_PREFIX, startRank, endRank);
        if (range != null && !range.isEmpty()) {
            return range.stream()
                    .map(o -> o instanceof Long ? (Long) o : Long.valueOf(o.toString()))
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    public List<PostGetRes> getTopPosts(Set<Long> topPostIds) {
        var postFromCache = this.getPosts(topPostIds);
        Set<Long> idsNotInCache = postService.filterIds(topPostIds, postFromCache);
        var postFromDB = postService.getTopPosts(idsNotInCache);
        //        for (PostGetRes postGetRes : data) {
        //            Long likesCount = this.getLikesCount(postGetRes.getId());
        //            postGetRes.setLikesCount(likesCount);
        //        }
        return Stream.concat(postFromCache.stream(), postFromDB.stream()).toList();
    }

    public List<PostGetRes> getPosts(Set<Long> ids) {
        Set<String> cacheKeys = ids.stream().map(RedisKeyConstants::getPostKey).collect(Collectors.toSet());
        List<Object> cacheValues = valueOps.multiGet(cacheKeys);
        if (cacheValues != null) {
            return cacheValues.stream()
                    .filter(Objects::nonNull)
                    .map(o -> (PostGetRes) o)
                    .toList();
        }
        return Collections.emptyList();
    }

    //    public boolean hasLiked(Long postId, Long userId) {
    //        String key = getPostLikesKey(postId);
    //        Object value = hashOps.get(key, userId.toString());
    //        if (value == null) {
    //            boolean isLiked = postLikeRepository.isPostLikedByUser(postId, userId);
    //            hashOps.put(key, userId.toString(), isLiked ? LIKE_STATUS : UNLIKE_STATUS);
    //            redisTemplate.expire(key, DEFAULT_CACHE_DURATION, TimeUnit.SECONDS);
    //            return isLiked;
    //        }
    //        return LIKE_STATUS.equals(value);
    //    }

    /**
     * The value retrieved from the cache is inconsistent with actually values
     */
    public Long getLikesCount(Long postId) {
        String countKey = getPostLikesCountKey(postId);
        Object count = valueOps.get(countKey);
        if (count == null) {
            Long dbCount = postLikeRepository.countById(postId);
            long randomExpiry = COUNT_CACHE_TTL + random.nextInt((int) MAX_RANDOM_EXPIRY);
            valueOps.set(countKey, dbCount.toString(), randomExpiry, TimeUnit.SECONDS);
            // Add to the top likes ZSet
            addPostToSortedSet(String.valueOf(postId), dbCount);
            return dbCount;
        }
        return Long.parseLong(count.toString());
    }
}
