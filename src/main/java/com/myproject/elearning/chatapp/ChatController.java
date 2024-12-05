package com.myproject.elearning.chatapp;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class ChatController {
    CustomWebSocketService customWebSocketService;
    private final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @MessageMapping("/chat")
    public void sendMessage(ChatMessage message) {
        logger.info("Received message: {}", message);
        String topic = message.getRoomId();
        message.setSender("a");
        message.setType(ChatMessage.MessageType.CHAT);
        message.setTimestamp(Instant.now());
        customWebSocketService.publish(ChannelTopic.of(topic), message);
    }


}
