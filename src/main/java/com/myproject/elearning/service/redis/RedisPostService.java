package com.myproject.elearning.service.redis;

import com.myproject.elearning.dto.request.post.PostLikeData;
import com.myproject.elearning.repository.custom.PostLikeRepositoryCustom;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RedisPostService {
    RedisTemplate<String, Object> redisTemplate;
    ValueOperations<String, Object> valueOps;
    PostLikeRepositoryCustom postLikeRepository;
    static final String POST_LIKE_KEY_FORMAT = "posts:%d:likes:%d";

    public RedisPostService(RedisTemplate<String, Object> redisTemplate, PostLikeRepositoryCustom postLikeRepository) {
        this.redisTemplate = redisTemplate;
        this.valueOps = redisTemplate.opsForValue();
        this.postLikeRepository = postLikeRepository;
    }

    private String getLikeKey(Long postId, Long userId) {
        return String.format(POST_LIKE_KEY_FORMAT, postId, userId);
    }

    public void toggleLike(Long postId, Long userId) {
        String key = getLikeKey(postId, userId);
        Object currentValue = valueOps.get(key);

        boolean isLiked = "1".equals(currentValue);

        if (isLiked) {
            valueOps.set(key, "0");
        } else {
            valueOps.set(key, "1");
        }
    }

    public Boolean hasLiked(Long postId, Long userId) {
        String key = getLikeKey(postId, userId);
        return "1".equals(valueOps.get(key));
    }

    public Long getLikesCount(Long postId) {
        String pattern = String.format("posts:%d:likes:*", postId);
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys == null) return 0L;

        long count = 0;
        for (String key : keys) {
            if ("1".equals(valueOps.get(key))) {
                count++;
            }
        }
        return count;
    }

    @Scheduled(cron = "0 */1 * * * *")
    @Transactional
    public void syncLikesToDatabase() {
        Set<String> allKeys = redisTemplate.keys("posts:*:likes:*");
        if (allKeys == null || allKeys.isEmpty()) return;

        Set<PostLikeData> likesToAdd = new HashSet<>();
        Set<PostLikeData> likesToRemove = new HashSet<>();

        for (String key : allKeys) {
            String[] parts = key.split(":");
            if (parts.length != 4) continue;

            Long postId = Long.parseLong(parts[1]);
            Long userId = Long.parseLong(parts[3]);
            PostLikeData postLikeData = new PostLikeData(postId, userId);
            String value = (String) valueOps.get(key);

            if ("1".equals(value)) {
                likesToAdd.add(postLikeData);
            } else if ("0".equals(value)) {
                likesToRemove.add(postLikeData);
            }
        }

        int[][] insertResults = !likesToAdd.isEmpty() ? postLikeRepository.bulkInsertLikes(likesToAdd) : null;
        int[][] deleteResults = !likesToRemove.isEmpty() ? postLikeRepository.bulkDeleteLikes(likesToRemove) : null;

        redisTemplate.delete(allKeys);
        logBatchResults(insertResults, deleteResults);
    }

    private void logBatchResults(int[][] insertResults, int[][] deleteResults) {
        if (insertResults != null) {
            for (int i = 0; i < insertResults.length; i++) {
                log.info("Batch insert {} time(s) {} records", i, insertResults[i].length);
            }
        }
        if (deleteResults != null) {
            for (int i = 0; i < deleteResults.length; i++) {
                log.info("Batch delete {} time(s) {} records", i, deleteResults[i].length);
            }
        }
    }
}
