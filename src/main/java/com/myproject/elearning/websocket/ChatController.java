package com.myproject.elearning.websocket;

import com.myproject.elearning.dto.request.chat.MessageData;
import com.myproject.elearning.service.ChatService;
import com.myproject.elearning.service.messaging.RedisPublisher;
import jakarta.validation.Valid;
import java.security.Principal;
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
    private final RedisPublisher redisPublisher;
    private final ChatService chatService;
    private final Logger logger = LoggerFactory.getLogger(ChatController.class);

    /**
     * Client gui tin nhan toi /pub/chat voi payload ChatMessage
     */
    @MessageMapping("/chat")
    public void sendMessage(@Valid MessageData message, Principal principal) {
        if (message.getRoomId() == null || message.getRoomId().isEmpty()) {
            logger.error("Room ID is null or empty");
            return;
        }

        if (!chatService.hasAccessToRoom(principal, message.getRoomId())) {
            logger.error("User {} does not have access to room {}", principal.getName(), message.getRoomId());
            return;
        }

        message.setSenderId(principal.getName());
        String topic = message.getRoomId();
        logger.info("Publishing message from {} to room {}", message.getSenderId(), topic);
        redisPublisher.publish(ChannelTopic.of(topic), message);
    }
}
