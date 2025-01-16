package com.myproject.elearning.rest.testing;

import com.myproject.elearning.dto.projection.UserAuth;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.service.CourseSearchService;
import com.myproject.elearning.service.redis.RedisAuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/test")
@RestController
public class TestController {
    UserRepository userRepository;
    RedisAuthService redisAuthService;
    CourseSearchService courseSearchService;

    @GetMapping("")
    public Object getUser() {
        String username = "admin1@example.com";
        Object cachedUser = redisAuthService.getCachedUser(username);
        System.out.println(cachedUser);

        if (cachedUser instanceof UserAuth userAuth) {
            return (userAuth);
        }

        UserAuth userAuth = userRepository.findAuthDTOByEmail(username).orElseGet(() -> {
            redisAuthService.setEmptyCache(username);
            throw new InvalidIdEx("Email not found");
        });

        redisAuthService.setCachedUser(username, userAuth);
        return userAuth;
    }

    @PostMapping("")
    public void setupMeilisearch() {
        courseSearchService.setupMeilisearch();
    }
}
