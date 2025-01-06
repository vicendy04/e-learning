package com.myproject.elearning.service.redis;

import static com.myproject.elearning.constant.RedisKeyConstants.*;

import com.myproject.elearning.dto.request.post.PostLikeData;
import com.myproject.elearning.repository.PostLikeRepositoryCustom;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RedisPostService {
    static final String LIKE_STATUS = "1";
    static final String UNLIKE_STATUS = "0";
    static final long CACHE_TTL = 24 * 60 * 60; // 1 day
    static final long COUNT_CACHE_TTL = 5 * 60; // 5 minutes
    RedisTemplate<String, Object> redisTemplate;
    HashOperations<String, String, Object> hashOps;
    ValueOperations<String, Object> valueOps;
    PostLikeRepositoryCustom postLikeRepository;

    public RedisPostService(
            RedisTemplate<String, Object> redisTemplate,
            HashOperations<String, String, Object> hashOps,
            ValueOperations<String, Object> valueOps,
            PostLikeRepositoryCustom postLikeRepository) {
        this.redisTemplate = redisTemplate;
        this.hashOps = hashOps;
        this.valueOps = valueOps;
        this.postLikeRepository = postLikeRepository;
    }

    public void like(Long postId, Long userId) {
        String key = getPostLikesKey(postId);
        String countKey = getPostLikesCountKey(postId);
        redisTemplate.multi();
        try {
            hashOps.put(key, userId.toString(), LIKE_STATUS);
            valueOps.increment(countKey);
            redisTemplate.expire(key, CACHE_TTL, TimeUnit.SECONDS);
            redisTemplate.exec();
        } catch (Exception e) {
            redisTemplate.discard();
            throw e;
        }
    }

    public void unlike(Long postId, Long userId) {
        String key = getPostLikesKey(postId);
        String countKey = getPostLikesCountKey(postId);
        redisTemplate.multi();
        try {
            hashOps.put(key, userId.toString(), UNLIKE_STATUS);
            valueOps.decrement(countKey);
            redisTemplate.expire(key, CACHE_TTL, TimeUnit.SECONDS);
            redisTemplate.exec();
        } catch (Exception e) {
            redisTemplate.discard();
            throw e;
        }
    }

    public boolean hasLiked(Long postId, Long userId) {
        String key = getPostLikesKey(postId);
        Object value = hashOps.get(key, userId.toString());
        if (value == null) {
            boolean isLiked = postLikeRepository.isPostLikedByUser(postId, userId);
            hashOps.put(key, userId.toString(), isLiked ? LIKE_STATUS : UNLIKE_STATUS);
            redisTemplate.expire(key, CACHE_TTL, TimeUnit.SECONDS);
            return isLiked;
        }
        return LIKE_STATUS.equals(value);
    }

    /**
     * The value retrieved from the cache is inconsistent with actually values
     */
    public Long getLikesCount(Long postId) {
        String countKey = getPostLikesCountKey(postId);
        Object count = valueOps.get(countKey);
        if (count == null) {
            Long dbCount = postLikeRepository.countByPostId(postId);
            valueOps.set(countKey, dbCount.toString(), COUNT_CACHE_TTL, TimeUnit.SECONDS);
            return dbCount;
        }
        return Long.parseLong(count.toString());
    }

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void syncLikesToDatabase() {
        Set<String> likeKeys = redisTemplate.keys("posts:*:likes");
        if (likeKeys == null || likeKeys.isEmpty()) return;

        Set<PostLikeData> likesToAdd = new HashSet<>();
        Set<PostLikeData> likesToRemove = new HashSet<>();

        for (String key : likeKeys) {
            Long postId = Long.parseLong(key.split(":")[1]);
            Map<String, Object> entries = hashOps.entries(key);

            entries.forEach((userId, value) -> {
                PostLikeData data = new PostLikeData(postId, Long.parseLong(userId));
                if (LIKE_STATUS.equals(value)) {
                    likesToAdd.add(data);
                } else if (UNLIKE_STATUS.equals(value)) {
                    likesToRemove.add(data);
                }
            });
        }

        if (!likesToAdd.isEmpty()) {
            postLikeRepository.bulkInsertLikes(likesToAdd);
        }
        if (!likesToRemove.isEmpty()) {
            postLikeRepository.bulkDeleteLikes(likesToRemove);
        }

        redisTemplate.delete(likeKeys);
    }
}
