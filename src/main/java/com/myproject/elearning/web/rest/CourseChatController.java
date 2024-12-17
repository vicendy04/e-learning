package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.request.chat.GroupChatCreateReq;
import com.myproject.elearning.dto.response.chat.GroupChatRes;
import com.myproject.elearning.service.GroupChatService;
import com.myproject.elearning.service.RedisSubscriber;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Todo: allow only teachers to create chat room
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/courses/chat")
@RestController
public class CourseChatController {
    Logger logger = LoggerFactory.getLogger(CourseChatController.class);
    GroupChatService groupChatService;
    RedisMessageListenerContainer redisMessageListener;
    RedisSubscriber redisSubscriber;

    /**
     * Tạo group chat trong database
     * Dang ky Redis listener cho group chat mới
     */
    @PostMapping("/room")
    public ResponseEntity<ApiRes<GroupChatRes>> createChatRoom(@Valid @RequestBody GroupChatCreateReq request) {
        GroupChatRes groupChat = groupChatService.createGroupChat(request);
        String channelName = "chat:" + groupChat.getId();
        ChannelTopic topic = ChannelTopic.of(channelName);
        redisMessageListener.addMessageListener(redisSubscriber, topic);
        logger.info("Created chat room for course: {}", request.getCourseId());
        ApiRes<GroupChatRes> response = successRes("Chat room created successfully", groupChat);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
