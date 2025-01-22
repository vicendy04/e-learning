package com.myproject.elearning.dto.request.user;

import java.util.Set;

public record UserPreferencesData(Long userId, Set<Long> topicIds) {}
