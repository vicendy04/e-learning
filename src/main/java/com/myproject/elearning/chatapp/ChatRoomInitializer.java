package com.myproject.elearning.chatapp;

import com.myproject.elearning.domain.GroupChat;
import com.myproject.elearning.repository.GroupChatRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class ChatRoomInitializer {
    Logger logger = LoggerFactory.getLogger(ChatRoomInitializer.class);
    RedisMessageListenerContainer redisMessageListenerContainer;
    RedisSubscriber redisSubscriber;
    GroupChatRepository groupChatRepository;

    @PostConstruct
    public void init() {
        List<GroupChat> groupChats = groupChatRepository.findAll();
        groupChats.forEach(groupChat -> {
            String channelName = "chat:" + groupChat.getId();
            ChannelTopic topic = ChannelTopic.of(channelName);
            redisMessageListenerContainer.addMessageListener(redisSubscriber, topic);
        });
        logger.info("Registered listener for all group chats");
    }
}
