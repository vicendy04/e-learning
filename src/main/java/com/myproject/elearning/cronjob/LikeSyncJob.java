package com.myproject.elearning.cronjob;

import static com.myproject.elearning.constant.RedisKeyConstants.LIKE_STATUS;
import static com.myproject.elearning.constant.RedisKeyConstants.UNLIKE_STATUS;

import com.myproject.elearning.dto.request.post.PostLikeData;
import com.myproject.elearning.repository.PostLikeRepositoryCustom;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class LikeSyncJob {
    PostLikeRepositoryCustom postLikeRepository;
    RedisTemplate<String, Object> redisTemplate;
    HashOperations<String, String, Object> hashOps;

    @Scheduled(cron = "0 0 2,14 * * *")
    @Transactional
    public void syncLikesToDatabase() {
        log.info("Syncing likes to database...");
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
        log.info("Sync completed successfully!");
    }
}
