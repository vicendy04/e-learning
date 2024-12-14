package com.myproject.elearning.chatapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.elearning.dto.request.chat.MessageCreateReq;
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

    /**
     * Gui toi cac clients dang subscribe (ws) /sub/chat/rooms/{roomId}
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // load from redis
            String publishMessage = new String(message.getBody());
            logger.info("Received message: {}", publishMessage);

            JsonNode jsonNode = objectMapper.readTree(publishMessage);
            MessageCreateReq chatMessage;
            if (jsonNode.isArray() && jsonNode.size() > 1) {
                chatMessage = objectMapper.treeToValue(jsonNode.get(1), MessageCreateReq.class);
            } else {
                chatMessage = objectMapper.readValue(publishMessage, MessageCreateReq.class);
            }

            String destination = "/sub/chat/rooms/" + chatMessage.getRoomId();
            logger.info("Forwarding message to WebSocket destination: {}", destination);

            messagingTemplate.convertAndSend(destination, chatMessage);
            logger.info("Message forwarded successfully");
        } catch (Exception e) {
            logger.error("Error processing message", e);
        }
    }
}
