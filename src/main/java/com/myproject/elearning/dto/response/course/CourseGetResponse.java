package com.myproject.elearning.dto.response.course;

import com.myproject.elearning.domain.Course;
import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseGetResponse {
    Long id;
    String title;
    String description;
    int duration;
    BigDecimal price;
    Course.CourseCategory category;
    int enrollmentCount;
    //     List<ContentDTO> contents;

    //    @Getter
    //    @Setter
    //    @NoArgsConstructor
    //    @AllArgsConstructor
    //    @FieldDefaults(level = AccessLevel.PRIVATE)
    //    public static class ContentDTO {
    //         Long id;
    //         String title;
    //         int orderIndex;
    //         String status;
    //    }
}
