package com.myproject.elearning.chatapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RedisSubscriber implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(RedisSubscriber.class);

    ObjectMapper objectMapper;
    SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        logger.info("Received message on topic: {}", new String(pattern));
        try {
            String publishMessage = new String(message.getBody());
            logger.info("Received message: {}", publishMessage);

            ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
            String destination = "/sub/" + chatMessage.getRoomId();

            logger.info("Sending to destination: {}", destination);
            messagingTemplate.convertAndSend(destination, chatMessage);
        } catch (Exception e) {
            logger.error("Error processing message", e);
        }
    }
}
