package com.myproject.elearning.dto.response.course;

import com.myproject.elearning.domain.Course;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopicCoursesRes {
    Long topicId;
    String topicName;
    List<CourseInfo> courses;

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CourseInfo {
        Long id;
        String title;
        Integer duration;
        Course.Level level;

        String thumbnailUrl;
        Integer enrolledCount;
        Integer reviewCount;
    }
}
