package com.myproject.elearning.dto.response.user;

import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PreferenceRes {
    Set<MyTopic> myTopics;

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class MyTopic {
        Long id;
        String name;
    }
}
