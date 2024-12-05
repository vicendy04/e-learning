package com.myproject.elearning.chatapp;

import com.myproject.elearning.dto.projection.UserAuthDTO;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.service.cache.RedisAuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/test")
@RestController
public class TestController {
    UserRepository userRepository;
    RedisAuthService redisAuthService;
    RedisMessageListenerContainer redisMessageListener;
    RedisSubscriber redisSubscriber;
    CustomWebSocketService customWebSocketService;
    Logger logger = LoggerFactory.getLogger(TestController.class);

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

    @PostMapping("/room/{roomId}")
    public void createChatRoom(@PathVariable String roomId) {
        ChannelTopic topic = ChannelTopic.of(roomId);
        logger.info("Creating chat room with ID: {}", roomId);

        redisMessageListener.addMessageListener(redisSubscriber, topic);
        logger.info("Added Redis subscriber for topic: {}", topic.getTopic());
    }

    @PostMapping("/room/{roomId}/test")
    public void testRoom(@PathVariable String roomId) {
        ChatMessage testMessage = ChatMessage.builder()
                .roomId(roomId)
                .content("Test connection message")
                .sender("System")
                .type(ChatMessage.MessageType.CHAT)
                .build();

        ChannelTopic topic = ChannelTopic.of(roomId);
        customWebSocketService.publish(topic, testMessage);
    }
}
