package com.myproject.elearning.config;

import com.myproject.elearning.domain.GroupChat;
import com.myproject.elearning.repository.GroupChatRepository;
import com.myproject.elearning.service.messaging.RedisSubscriber;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class ChatInit {
    RedisSubscriber redisSubscriber;
    GroupChatRepository groupChatRepository;
    RedisMessageListenerContainer redisMessageListenerContainer;

    @PostConstruct
    public void init() {
        List<GroupChat> groupChats = groupChatRepository.findAll();
        groupChats.forEach(groupChat -> {
            String channelName = "chat:" + groupChat.getId();
            ChannelTopic topic = ChannelTopic.of(channelName);
            redisMessageListenerContainer.addMessageListener(redisSubscriber, topic);
        });
        log.info("Registered listener for all group chats");
    }
}
