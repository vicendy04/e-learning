package com.myproject.elearning.service;

import static com.myproject.elearning.mapper.TopicMapper.TOPIC_MAPPER;

import com.myproject.elearning.domain.Topic;
import com.myproject.elearning.dto.request.user.UserPreferencesData;
import com.myproject.elearning.dto.response.user.PreferenceRes;
import com.myproject.elearning.exception.problemdetails.EmailUsedEx;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
import com.myproject.elearning.repository.TopicRepository;
import com.myproject.elearning.repository.UserRepository;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserPreferenceService {
    UserRepository userRepository;
    TopicRepository topicRepository;

    @Transactional
    public void setInitialPreferences(Long userId, Set<Long> topicIds) {
        validateUserPreferencesNotSet(userId);
        validateAllTopicsExist(topicIds);
        userRepository.bulkAddPref(new UserPreferencesData(userId, topicIds));
    }

    private void validateUserPreferencesNotSet(Long userId) {
        if (userRepository.countUserPreferences(userId) > 0)
            throw new EmailUsedEx("Preferences already set for this user");
    }

    private void validateAllTopicsExist(Set<Long> topicIds) {
        if (topicRepository.countByIdIn(topicIds) != topicIds.size()) throw new InvalidIdEx("Some topics not found");
    }

    @Transactional
    public void updatePreferences(Long userId, Set<Long> addTopicIds, Set<Long> delTopicIds) {
        if (!delTopicIds.isEmpty()) userRepository.delUserPreferences(userId, delTopicIds);
        if (!addTopicIds.isEmpty()) {
            Set<Topic> addTopics = topicRepository.findAllByIdIn(addTopicIds);
            if (addTopics.size() != addTopicIds.size()) throw new EmailUsedEx("Some topics to add not found");
            userRepository.bulkAddPref(new UserPreferencesData(userId, addTopicIds));
        }
    }

    @Transactional(readOnly = true)
    public PreferenceRes getMyPreferences(Long userId) {
        Set<Topic> topics = userRepository.getMyPreferences(userId);
        return TOPIC_MAPPER.toRes(topics);
    }
}
