package com.myproject.elearning.service;

import com.myproject.elearning.domain.GroupChat;
import com.myproject.elearning.dto.request.chat.GroupChatCreateReq;
import com.myproject.elearning.dto.response.chat.GroupChatRes;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
import com.myproject.elearning.mapper.GroupChatMapper;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.repository.GroupChatRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class GroupChatService {
    GroupChatRepository groupChatRepository;
    CourseRepository courseRepository;
    GroupChatMapper groupChatMapper;

    // check xem course co dung la cua teacher ko?
    @Transactional
    public GroupChatRes createGroupChat(GroupChatCreateReq request) {
        if (!courseRepository.existsById(request.getCourseId())) {
            throw new InvalidIdEx(request.getCourseId());
        }
        if (groupChatRepository.existsByCourseId(request.getCourseId())) {
            throw new InvalidIdEx("Khóa học này đã có nhóm chat");
        }
        GroupChat groupChat = groupChatMapper.toEntity(request);
        return groupChatMapper.toRes(groupChatRepository.save(groupChat));
    }

    public GroupChatRes getGroupChatByCourse(Long courseId) {
        GroupChat groupChat = groupChatRepository.findByCourseId(courseId).orElseThrow(() -> new InvalidIdEx(courseId));
        return groupChatMapper.toRes(groupChat);
    }
}
