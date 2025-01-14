package com.myproject.elearning.service.redis;

import static com.myproject.elearning.constant.RedisKeyConstants.getOwnershipKey;

import com.myproject.elearning.constant.ResourceType;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RedisResourceAccessService {
    static final long DEFAULT_CACHE_DURATION = 1800; // 30 min
    static final long MAX_RANDOM_EXPIRY = 300; // 5 min

    RedisTemplate<String, Object> redisTemplate;
    ValueOperations<String, Object> valueOps;
    Random random;

    public Boolean getCachedOwnership(ResourceType type, Long resourceId, Long userId) {
        String key = getOwnershipKey(type.value(), resourceId, userId);
        Object value = valueOps.get(key);
        return value != null ? (Boolean) value : null;
    }

    public void setCachedOwnership(ResourceType type, Long resourceId, Long userId, boolean isOwner) {
        String key = getOwnershipKey(type.value(), resourceId, userId);
        long randomExpiry = DEFAULT_CACHE_DURATION + random.nextInt((int) MAX_RANDOM_EXPIRY);
        valueOps.set(key, isOwner, randomExpiry, TimeUnit.SECONDS);
    }

    /**
     * No need to invalidate ownership cache when deleting a course because:
     * 1. Each course belongs to only one user
     * 2. If the course is deleted, the system will return false when checking ownership, whether the cache is invalidated or not:
     * - Without invalidating the cache: If the course is not found in the cache, it will return false when checking ownership.
     * - With invalidating the cache: A cache miss will occur, leading to a DB check, which will also return false if the course doesn't exist.
     */
    public void invalidateOwnershipCache(ResourceType type, Long resourceId, Long userId) {
        String key = getOwnershipKey(type.value(), resourceId, userId);
        if (key != null && !key.isEmpty()) {
            redisTemplate.delete(key);
        }
    }
}
