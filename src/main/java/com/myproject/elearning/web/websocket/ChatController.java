package com.myproject.elearning.web.websocket;

import com.myproject.elearning.dto.request.chat.MessagePayload;
import com.myproject.elearning.service.RedisPublisher;
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
    RedisPublisher redisPublisher;
    private final Logger logger = LoggerFactory.getLogger(ChatController.class);

    /**
     * Client gui tin nhan toi /pub/chat voi payload ChatMessage
     */
    @MessageMapping("/chat")
    public void sendMessage(MessagePayload message) {
        if (message.getRoomId() == null || message.getRoomId().isEmpty()) {
            logger.error("Room ID is null or empty");
            return;
        }
        String topic = message.getRoomId();
        logger.info("Publishing message to topic {}: {}", topic, message);
        redisPublisher.publish(ChannelTopic.of(topic), message);
    }
}
