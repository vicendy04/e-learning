package com.myproject.elearning.cronjob;

import com.myproject.elearning.service.cache.PostRedisService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class PostCleanupJob {
    PostRedisService postRedisService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanUpPosts() {
        log.info("Running cron job to clean up posts...");
        postRedisService.removePostsExceptTopN(100);
        log.info("Clean up completed!");
    }
}
