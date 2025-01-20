package com.myproject.elearning.rest.content;

import static com.myproject.elearning.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.request.chat.GroupChatCreateReq;
import com.myproject.elearning.dto.response.chat.GroupChatRes;
import com.myproject.elearning.service.GroupChatService;
import com.myproject.elearning.service.messaging.RedisSubscriber;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/courses/chat")
@RestController
public class CourseChatController {
    RedisSubscriber redisSubscriber;
    GroupChatService groupChatService;
    RedisMessageListenerContainer redisMessageListener;

    /**
     * Tạo group chat trong database
     * Dang ky Redis listener cho group chat mới
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/room")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ApiRes<GroupChatRes> createChatRoom(@Valid @RequestBody GroupChatCreateReq request) {
        GroupChatRes groupChat = groupChatService.createGroupChat(request);
        String channelName = "chat:" + groupChat.getId();
        ChannelTopic topic = ChannelTopic.of(channelName);
        redisMessageListener.addMessageListener(redisSubscriber, topic);
        log.info("Created chat room for course: {}", request.getCourseId());
        return successRes("Chat room created successfully", groupChat);
    }
}
