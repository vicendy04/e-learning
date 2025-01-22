package com.myproject.elearning.dto.request.user;

import jakarta.validation.constraints.AssertTrue;
import java.util.Collections;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EditPreferencesReq {
    Set<Long> addTopicIds;
    Set<Long> delTopicIds;

    @AssertTrue(message = "Cannot add and remove the same topic")
    public boolean isValidTopicModification() {
        return Collections.disjoint(addTopicIds, delTopicIds);
    }
}
