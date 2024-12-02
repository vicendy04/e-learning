package com.myproject.elearning.web.rest;

import com.myproject.elearning.dto.projection.UserAuthDTO;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.service.cache.RedisAuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/test")
@RestController
public class TestController {
    private final UserRepository userRepository;
    private final RedisAuthService redisAuthService;

    @GetMapping("")
    public Object getUser() {
        String username = "admin1@example.com";
        Object cachedUser = redisAuthService.getCachedUser(username);
        System.out.println(cachedUser);

        if (cachedUser instanceof String s) {
            if ("empty".equals(s)) {
                throw new InvalidIdException("Email not found");
            }
        }

        if (cachedUser instanceof UserAuthDTO userAuthDTO) {
            return (userAuthDTO);
        }

        UserAuthDTO userAuthDTO = userRepository.findAuthDTOByEmail(username).orElseGet(() -> {
            redisAuthService.setEmptyCache(username);
            throw new InvalidIdException("Email not found");
        });

        redisAuthService.setCachedUser(username, userAuthDTO);
        return userAuthDTO;
    }
}
