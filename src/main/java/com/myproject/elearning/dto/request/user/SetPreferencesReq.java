package com.myproject.elearning.dto.request.user;

import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SetPreferencesReq {
    Set<Long> topicIds;
}
