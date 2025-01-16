package com.myproject.elearning.cronjob;

import com.myproject.elearning.service.redis.RedisPostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class PostCleanupJob {
    RedisPostService redisPostService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanUpPosts() {
        System.out.println("Running cron job to clean up posts...");
        redisPostService.removePostsExceptTopN(100);
        System.out.println("Clean up completed!");
    }
}
