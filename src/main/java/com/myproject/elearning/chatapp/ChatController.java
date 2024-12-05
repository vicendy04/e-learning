package com.myproject.elearning.chatapp;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class ChatController {
    CustomWebSocketService customWebSocketService;
    private final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @MessageMapping("/chat")
    public void sendMessage(ChatMessage message) {
        logger.debug("Raw message received: {}", message);

        if (message.getRoomId() == null || message.getRoomId().isEmpty()) {
            logger.error("Room ID is null or empty");
            return;
        }

        String topic = message.getRoomId();

        logger.info("Publishing message to topic {}: {}", topic, message);
        customWebSocketService.publish(ChannelTopic.of(topic), message);
    }
}
