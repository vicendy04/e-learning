package com.myproject.elearning.service.redis;

import com.myproject.elearning.dto.request.post.PostLikeData;
import com.myproject.elearning.repository.PostLikeRepositoryCustom;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RedisPostService {
    RedisTemplate<String, Object> redisTemplate;
    HashOperations<String, String, Object> hashOps;
    PostLikeRepositoryCustom postLikeRepository;
    static final String POST_LIKES_KEY_FORMAT = "posts:%d:likes";

    public RedisPostService(
            RedisTemplate<String, Object> redisTemplate,
            HashOperations<String, String, Object> hashOps,
            PostLikeRepositoryCustom postLikeRepository) {
        this.redisTemplate = redisTemplate;
        this.hashOps = hashOps;
        this.postLikeRepository = postLikeRepository;
    }

    private String getLikeKey(Long postId) {
        return String.format(POST_LIKES_KEY_FORMAT, postId);
    }

    public void like(Long postId, Long userId) {
        String key = getLikeKey(postId);
        hashOps.put(key, userId.toString(), "1");
    }

    public void unlike(Long postId, Long userId) {
        String key = getLikeKey(postId);
        hashOps.put(key, userId.toString(), "0");
    }

    /**
     * @return {@code true} if the user has liked the post,
     * {@code false} if the user has not liked the post,
     * or {@code null} if the like status is not found (tips: query db).
     */
    public Boolean hasLiked(Long postId, Long userId) {
        String key = getLikeKey(postId);
        Object value = hashOps.get(key, userId.toString());
        return value == null ? null : "1".equals(value);
    }

    public void setLikeStatus(Long postId, Long userId, boolean likeStatus) {
        String key = getLikeKey(postId);
        hashOps.put(key, userId.toString(), likeStatus ? "1" : "0");
    }

    public Long getLikesCount(Long postId) {
        String key = getLikeKey(postId);
        Map<String, Object> entries = hashOps.entries(key);
        return entries.values().stream().filter("1"::equals).count();
    }

    @Scheduled(cron = "0 */1 * * * *")
    @Transactional
    public void syncLikesToDatabase() {
        Set<String> allKeys = redisTemplate.keys("posts:*:likes");
        if (allKeys == null || allKeys.isEmpty()) return;

        Set<PostLikeData> likesToAdd = new HashSet<>();
        Set<PostLikeData> likesToRemove = new HashSet<>();

        for (String key : allKeys) {
            Long postId = Long.parseLong(key.split(":")[1]);
            Map<String, Object> entries = hashOps.entries(key);

            entries.forEach((userId, value) -> {
                PostLikeData postLikeData = new PostLikeData(postId, Long.parseLong(userId));

                if ("1".equals(value)) {
                    likesToAdd.add(postLikeData);
                } else if ("0".equals(value)) {
                    likesToRemove.add(postLikeData);
                }
            });
        }

        if (!likesToAdd.isEmpty()) {
            postLikeRepository.bulkInsertLikes(likesToAdd);
        }
        if (!likesToRemove.isEmpty()) {
            postLikeRepository.bulkDeleteLikes(likesToRemove);
        }

        redisTemplate.delete(allKeys);
    }
}
