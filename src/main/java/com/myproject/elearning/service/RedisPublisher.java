package com.myproject.elearning.service;

import com.myproject.elearning.dto.request.chat.MessagePayload;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RedisPublisher {
    RedisTemplate<String, Object> redisTemplate;

    /**
     * Publish tin nhan den Redis topic tuong ung
     */
    public void publish(ChannelTopic topic, MessagePayload message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
